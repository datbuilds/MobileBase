package vn.shb.lao.screens.login.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
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
import vn.shb.lao.utils.extensions.clearEditTextColorFilter
import vn.shb.lao.utils.extensions.clearText
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.nextActivity
import vn.shb.lao.utils.extensions.setErrorAndBackground
import vn.shb.lao.utils.extensions.setErrorAndBackgroundDefault
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.extensions.textValue
import vn.shb.lao.utils.extensions.visible
import vn.shb.lao.utils.view.dialog.AlertDialogUtil
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val storage: AndroidSecureStorage by inject()
    private val viewModel: LoginViewModel by inject()
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()

    private var currentUser: UserInfo? = null
    var currentUserName = ""

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun initView(view: View) {
        mapUILogin()
        setSingleView()
    }

    private fun setSingleView() {
        // underline text
        binding.tvForgotPassword.paintFlags =
            binding.tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun mapUILogin() {
        // set banner
//        val bgLogin = storage.getBackgroundLogin()
//        val bgBitmap: Bitmap? =
//            if (bgLogin.isNotEmpty()) {
//                prepareBGLogin(bgLogin)
//            } else {
//                val drawable =
//                    ContextCompat.getDrawable(requireContext(), R.drawable.img_bg_login_res)
//                if (drawable is BitmapDrawable) drawable.bitmap else drawable?.toBitmap()
//            }

        UserConverters.stringToUserInfo(storage.getUserInfo())?.let { user ->
            prepareViewUserLogged(user)
        } ?: resetInputLogin()

        storage.resetToken()
    }

    @SuppressLint("NewApi")
    private fun prepareBGLogin(bgBase64: String): Bitmap? {
        val default = Base64.getDecoder().decode(bgBase64)
        return BitmapFactory.decodeByteArray(default, 0, default.size)
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
                edtInputUsername.clearFocus()
                edtInputPass.clearFocus()
                hideSoftKeyboard(0)
            }
            edtInputUsername.setOnFocusChangeListener { _, hasFocus ->
                inputUserNameLayout.isSelected = hasFocus
            }
            edtInputPass.setOnFocusChangeListener { _, hasFocus ->
                inputPasswordLayout.isSelected = hasFocus
            }

            edtInputPass.apply {
                doOnTextChanged { _, _, _, _ ->
                    binding.inputPasswordLayout.apply {
                        setErrorAndBackgroundDefault()
                        error = null
                        isErrorEnabled = false
                    }
                }

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        login()
                        true
                    } else {
                        false
                    }
                }
            }

            tvForgotPassword.setOnSingleClickListener {
                context?.let { ct -> viewModel.showDialogForgotPassword(ct) }
            }

            btnLogin.setOnSingleClickListener {
                AlertDialogUtil.message(
                    context!!,
                    title = getString(vn.shb.lao.localization.R.string.notification_channel_id),
                    message = "Chức năng đang được phát triển",
                    idIcon = vn.shb.lao.ui.R.drawable.ic_alert_forgot_password,
                    textNegative = getString(R.string.closeLabel),
                    negativeAction = {}
                )
            }
        }
    }

    private fun login() {
        hideSoftKeyboard(0)
        currentUser?.let {
            val (psw, encPsw) = getPassword()
            val userName = it.userLog.ifEmpty { it.username }

            if (psw.isEmpty()) {
                binding.inputPasswordLayout.setErrorAndBackground("Vui lòng nhập mật khẩu")
            } else {
                postLogin(userName, encPsw)
            }
        } ?: loginNewUser()
    }

    private fun loginNewUser() {
//        val userName = binding.userNameView.getUserName()
//
//        val isValidUserName = binding.userNameView.validateLogin(isValidate = true)
//        val isValidPassword = binding.inputPasswordLayout.validateLogin()
//
//        if (isValidUserName && isValidPassword) {
//            val (_, encPsw) = getPassword()
//            postLogin(userName, encPsw)
//        } else {
//            binding.userNameView.clearEditTextColorFilter()
//            binding.inputPasswordLayout.clearEditTextColorFilter()
//        }
    }

    private fun postLogin(us: String, psW: String) {
        val params = UseCaseLogin.Params(us, psW)
        viewModel.login(params)
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
                viewModel.stateLogin.collectLatest { uiState ->
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
                    viewModel.clearLoginState()
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
            tvImageUser.text = currentUserName.getInitials()
            setGreeting(binding.tvHelloUser)
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun onLoginSuccess(stateLogin: StateLogin) {
        if (stateLogin is StateLogin.OpenDashboard) {
            nextDashboard()
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
