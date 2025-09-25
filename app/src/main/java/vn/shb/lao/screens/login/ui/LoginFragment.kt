package vn.shb.lao.screens.login.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.domain.usecases.login.StateLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getInitials
import vn.shb.data.entities.login.UserConverters
import vn.shb.data.entities.login.UserInfo
import vn.shb.lao.R
import vn.shb.lao.activity.dashboard.DashboardActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentLoginBinding
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.screens.login.ui.widget.setGreeting
import vn.shb.lao.screens.login.ui.widget.showLanguagePopup
import vn.shb.lao.utils.extensions.clearEditTextColorFilter
import vn.shb.lao.utils.extensions.clearText
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.isValidInputLogin
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.nextActivity
import vn.shb.lao.utils.extensions.setCustomSpannable
import vn.shb.lao.utils.extensions.setErrorAndBackgroundDefault
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.extensions.textValue
import vn.shb.lao.utils.extensions.visible
import vn.shb.lao.utils.widgets.LocaleHelper
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val loginViewModel: LoginViewModel by inject()
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()

    private var currentUser: UserInfo? = null
    var currentUserName = ""

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun initView(view: View) {
        mapUILogin()
    }

    private fun mapUILogin() {
        //init forgot password
        binding.tvForgotPassword.setCustomSpannable(
            getString(R.string.forgotPasswordLabel), R.color.forgotPassword,
            R.color.forgotPasswordClick
        ) {
            context?.let { ct -> loginViewModel.showDialogForgotPassword(ct) }
        }

        // set icon current language
        LocaleHelper.getResourceLocale(LocaleHelper.getCurrentLanguage(requireContext())) { resId ->
            binding.ivLogoLanguage.setImageResource(resId)
        }

        //init user login
        UserConverters.stringToUserInfo(storage.getUserInfo())?.let { user ->
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

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(binding) {
            root.setOnSingleClickListener {
                clearFocusEditText()
            }
            edtInputUsername.setOnFocusChangeListener { _, hasFocus ->
                inputUserNameLayout.isSelected = hasFocus
            }
            edtInputPass.setOnFocusChangeListener { _, hasFocus ->
                inputPasswordLayout.isSelected = hasFocus
            }

            edtInputPass.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login()
                    true
                } else {
                    false
                }
            }

            btnLogin.setOnSingleClickListener {
                clearFocusEditText()
                handleActionLogin()
            }

            llLanguage.setOnSingleClickListener {
                showLanguagePopup(binding.llLanguage)
            }
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
                inputUserNameLayout.setErrorAndBackgroundDefault()
                if (!inputPasswordLayout.isValidInputLogin()) {
                    tvErrorPassword.visible()
                } else {
                    tvErrorPassword.gone()
                    inputPasswordLayout.setErrorAndBackgroundDefault()
//                    login()
                    UserInfo(username = "lingard", token_type = "34242323").toUserString().let { storage.setUserInfo(it) }
                    onLoginSuccess(StateLogin.OpenDashboard) //todo test dashboard
                }
            }
        }
    }

    private fun login() {
        hideSoftKeyboard(0)
        val userName = currentUser?.let { it.userLog.ifEmpty { it.username } }
            ?: binding.edtInputUsername.text?.trim().toString()
        val (_, encPsw) = getPassword()
        postLogin(userName, encPsw)
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
                            showDialogError(message = uiState.reason.message)
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

    private fun resetInputLogin() {
        with(binding) {
            llInfoUser.gone()
            groupViewNoLastUser.visible()
            inputPasswordLayout.clearText()
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun prepareViewUserLogged(user: UserInfo) {
        currentUser = user
        currentUserName = user.username

        binding.apply {
            llInfoUser.visible()
            groupViewNoLastUser.gone()
            flAvatarUser.setUserName("", currentUserName)
            setGreeting(binding.tvHelloUser)
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
            restartApp(activity!!)
        }

    }

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        clearFlag()
    }

    companion object {
        const val TAG = "LoginFragment"

    }
}
