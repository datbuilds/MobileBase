package vn.shb.lao.screens.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.delivery.reason.LoginFailReason
import vn.shb.core.core.domain.usecases.login.StateLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.login.UserLog
import vn.shb.lao.R
import vn.shb.lao.activity.dashboard.DashboardActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentLoginBinding
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.screens.login.ui.widget.showLanguagePopup
import vn.shb.lao.utils.extensions.clearEditTextColorFilter
import vn.shb.lao.utils.extensions.clearText
import vn.shb.lao.utils.extensions.getTextWelcomeUser
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.isValidInputLogin
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.nextActivity
import vn.shb.lao.utils.extensions.setCustomSpannable
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.extensions.textValue
import vn.shb.lao.utils.extensions.visible
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.lao.utils.widgets.LocaleHelper
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val loginViewModel: LoginViewModel by inject()
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()

    private var currentUser: UserLog? = null
    var currentUserName = ""

    private var isVisiblePassword = false

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun initView(view: View) {
    }

    private fun mapUILogin() {
        //init forgot password
        binding.tvForgotPassword.setCustomSpannable(
            getString(R.string.forgotPassword), R.color.forgotPassword,
            R.color.forgotPasswordClick
        ) {
            context?.let { ct -> loginViewModel.showDialogForgotPassword(ct) }
        }

        // set icon current language
        LocaleHelper.getResourceLocale(LocaleHelper.getCurrentLanguage(requireContext())) { resId ->
            binding.ivLogoLanguage.setImageResource(resId)
        }

        //init user login
        getCurrentUser()?.let { user ->
            prepareViewUserLogged(user)
        } ?: resetInputLogin()

        storage.resetToken()
    }

    override fun handleSavedState(savedInstanceState: Bundle?) {
        super.handleSavedState(savedInstanceState)
        if (savedInstanceState == null) {
            clearFlag()
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //        checkNotificationPermission()
    }

    override fun initListener() {
        with(binding) {
            root.setOnSingleClickListener {
                clearFocusEditText()
            }

            //handle edit username
            btnLogin.setOnSingleClickListener {
                clearFocusEditText()
                handleActionLogin()
            }
            edtInputUsername.setOnFocusChangeListener { _, hasFocus ->
                userNameContainer.isSelected = hasFocus
            }

            btnClearUsername.setOnClickListener {
                edtInputUsername.text?.clear()
            }
            edtInputUsername.addTextChangedListener {
                btnClearUsername.isVisible = !it.isNullOrEmpty()
            }

            //handle edit password
            edtInputPass.setOnFocusChangeListener { _, hasFocus ->
                passwordContainer.isSelected = hasFocus
            }
            edtInputPass.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login()
                    true
                } else {
                    false
                }
            }

            btnToggle.setOnClickListener {
                isVisiblePassword = !isVisiblePassword
                bindEdtPassword()
            }

            // Clear text
            btnClearPassword.setOnClickListener {
                edtInputPass.text?.clear()
            }
            edtInputPass.addTextChangedListener {
                btnClearPassword.isVisible = !it.isNullOrEmpty()
                btnToggle.isVisible = !it.isNullOrEmpty()
            }

            llLanguage.setOnSingleClickListener {
                showLanguagePopup(binding.llLanguage)
            }
        }
    }

    private fun bindEdtPassword() {
        with(binding) {
            val currentFont = edtInputPass.typeface
            if (isVisiblePassword) {
                edtInputPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggle.text = getString(R.string.hide)
            } else {
                edtInputPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggle.text = getString(R.string.show)
            }
            edtInputPass.typeface = currentFont
            edtInputPass.setSelection(edtInputPass.text?.length ?: 0)
        }
    }

    private fun clearFocusEditText() {
        binding.edtInputUsername.clearFocus()
        binding.edtInputPass.clearFocus()
        hideSoftKeyboard(0)
    }

    private fun handleActionLogin() {
        binding.apply {
            if (!inputUserNameLayout.isValidInputLogin() && currentUser == null) {
                tvErrorUsername.visible()
            } else {
                tvErrorUsername.gone()
                if (!inputPasswordLayout.isValidInputLogin()) {
                    tvErrorPassword.visible()
                } else {
                    tvErrorPassword.gone()
                    login()
                }
            }
        }
    }

    private fun login() {
        hideSoftKeyboard(0)
        val accountLogin = currentUser?.userLogin
            ?: binding.edtInputUsername.text?.trim().toString()
        val (_, encPsw) = getPassword()
        postLogin(accountLogin, encPsw)
    }

    private fun postLogin(us: String, psW: String) {
        val params = UseCaseLogin.Params(us, psW)
        loginViewModel.login(params)
    }

    private fun getPassword(): Pair<String, String> {
        val psw = binding.edtInputPass.textValue()
        val pswEncrypt = encryptFactory.encryptRSA(plainText = psw)
        val encPsw = Base64.getEncoder().encodeToString(pswEncrypt)
        return Pair(psw, encPsw)
    }

    private fun nextDashboard() {
        nextActivity(DashboardActivity.intent(requireContext()))
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                loginViewModel.stateLogin.collectLatest { uiState ->
                    when (uiState) {
                        LoginUiState.Idle -> {}
                        LoginUiState.Loading -> {
                            showProgressDialog()
                        }

                        is LoginUiState.Error -> {
                            hideProgressDialog()
                            if (uiState.reason is LoginFailReason){
                                showDialogErrorLockUser(uiState.reason)
                            } else {
                                showDialogError(
                                    reason = uiState.reason
                                )
                            }
                        }

                        is LoginUiState.Success -> {
                            hideProgressDialog()
                            onLoginSuccess(uiState.state)
                        }
                    }
                    loginViewModel.clearLoginState()
                }
            }
        }
    }

    private fun showDialogErrorLockUser(reason: LoginFailReason) {
        context?.let { BottomSheetDialogHelper(it).messageLoginFail(reason.errMessage, reason.lockedUntil) }
    }

    private fun resetInputLogin() {
        with(binding) {
            llInfoUser.gone()
            groupViewNoLastUser.visible()
            inputPasswordLayout.clearText()
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun prepareViewUserLogged(user: UserLog) {
        currentUser = user
        currentUserName = user.username

        binding.apply {
            llInfoUser.visible()
            groupViewNoLastUser.gone()
            flAvatarUser.setUserName(getPathAvatarUser(user.customerId), currentUserName)
            binding.tvHelloUser.text = requireContext().getTextWelcomeUser()
            binding.tvNameUser.text = currentUserName
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun onLoginSuccess(stateLogin: StateLogin) {
        if (stateLogin is StateLogin.OpenDashboard) {
            nextDashboard()
        }
    }

    fun updateLanguage(type: String) {
        context?.let { ct ->
            LocaleHelper.saveLanguage(ct, type)
            LocaleHelper.setLocale(ct, type)
            restartApp(requireActivity())
        }
    }

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        mapUILogin()
        bindEdtPassword()
        clearFlag()

        //mock
        binding.edtInputPass.setText("123456")
//        handleActionLogin()
    }

    companion object {
        const val TAG = "LoginFragment"

    }
}
