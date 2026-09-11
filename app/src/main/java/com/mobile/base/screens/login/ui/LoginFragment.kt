package com.mobile.base.screens.login.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.mobile.base.BuildConfig
import com.mobile.base.R
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.base.dialog.DialogSessionExpire
import com.mobile.base.databinding.FragmentLoginBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.navigation.requireNavigator
import com.mobile.base.screens.login.state.LoginUiState
import com.mobile.base.screens.login.ui.widget.ConfirmDeviceView
import com.mobile.base.screens.login.ui.widget.showLanguagePopup
import com.mobile.base.utils.ApiConst
import com.mobile.base.utils.extensions.checkShowProgressDialog
import com.mobile.base.utils.extensions.clearEditTextColorFilter
import com.mobile.base.utils.extensions.clearText
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.getTextWelcomeUser
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.hideProgressDialog
import com.mobile.base.utils.extensions.hideSoftKeyboard
import com.mobile.base.utils.extensions.isValidInputLogin
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.extensions.setOnMaterialButtonClick
import com.mobile.base.utils.extensions.textValue
import com.mobile.base.utils.extensions.visible
import com.mobile.base.utils.view.dialog.BottomSheetDialogHelper
import com.mobile.base.utils.view.dialog.CountdownBottomSheetDialog
import com.mobile.base.utils.view.dialog.ForceUpdateDialog
import com.mobile.base.utils.view.dialog.RegisterDeviceDialog
import com.mobile.base.utils.widgets.LocaleHelper
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.delivery.reason.LoginFailLocked
import com.mobile.base.core.core.delivery.reason.LoginRegisterDevice
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.security.encrypt.EncryptManager
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.login.RegisterDeviceData
import com.mobile.base.data.entities.login.UserLog
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val loginViewModel: LoginViewModel by inject()

    private var currentUser: UserLog? = null
    var currentUserName = ""

    private var isVisiblePassword = false

    // Track xem user đã thực sự tương tác với field chưa
    // Tránh hiện error khi recreate activity lúc đổi ngôn ngữ
    private var hasUserTypedUsername = false
    private var hasUserTypedPassword = false

    private lateinit var confirmDeviceView: ConfirmDeviceView

    private val codeNeedShowRedText = listOf(ApiConst.OTP_001, ApiConst.OTP_002, ApiConst.OTP_003)

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val logoTopMargin = resources.getDimensionPixelSize(R.dimen.paddingTopLayout)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            binding.ivAppLogo.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = logoTopMargin + cutout.top
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        binding.tvHotline.text =
            getString(R.string.version).plus(Const.SEPARATOR_SPACE).plus(BuildConfig.VERSION_NAME)
        storage.resetToken()

        // Initialize embedded confirm device view
        confirmDeviceView = ConfirmDeviceView(requireContext())
        binding.flRegisterDevice.addView(confirmDeviceView)
        confirmDeviceView.visibility = View.GONE

        // Bind remembered user state before the fragment becomes visible to avoid layout jumps.
        mapUILogin()
        bindEdtPassword()
        clearFlag()

        if (arguments?.getBoolean(AppDestination.ARG_SHOW_SESSION_EXPIRED) == true) {
            arguments?.putBoolean(AppDestination.ARG_SHOW_SESSION_EXPIRED, false)
            DialogSessionExpire().show(requireContext())
        }
        (activity as? MainActivity)?.removeCallbackTimeout()
    }

    private fun mapUILogin() {
        // set icon current language
        LocaleHelper.getResourceLocale(LocaleHelper.getCurrentLanguage(requireContext())) { resId ->
            binding.ivLogoLanguage.setImageResource(resId)
        }

        //init user login
        getCurrentUser()?.let { user ->
            prepareViewUserLogged(user)
        } ?: resetInputLogin()
    }

    override fun handleSavedState(savedInstanceState: Bundle?) {
        super.handleSavedState(savedInstanceState)
        if (savedInstanceState == null) {
            clearFlag()
        }
    }

    override fun initListener() {
        with(binding) {
            root.setOnSingleClickListener {
                clearFocusEditText()
            }

            //handle edit username
            btnLogin.setOnMaterialButtonClick {
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
                if (!it.isNullOrEmpty()) hasUserTypedUsername = true
                btnClearUsername.isVisible = !it.isNullOrEmpty()
                // Chỉ show error khi user đã từng gõ rồi xóa hết (không show khi recreate)
                tvErrorUsername.isVisible =
                    hasUserTypedUsername && it.isNullOrEmpty() && currentUser == null
            }

            //handle edit password
            edtInputPass.setOnFocusChangeListener { _, hasFocus ->
                passwordContainer.isSelected = hasFocus
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
                if (!it.isNullOrEmpty()) hasUserTypedPassword = true
                btnClearPassword.isVisible = !it.isNullOrEmpty()
                btnToggle.isVisible = !it.isNullOrEmpty()
                // Chỉ show error khi user đã từng gõ rồi xóa hết (không show khi recreate)
                tvErrorPassword.isVisible = edtInputPass.hasFocus() && it.isNullOrEmpty()
            }

            llLanguage.setOnSingleClickListener {
                showLanguagePopup(binding.llLanguage)
            }

            ivAppLogo.setOnSingleClickListener {
                resetInputLogin()
            }

            if (BuildConfig.DEBUG) {
//                binding.edtInputUsername.setText("0101030322")
//                binding.edtInputPass.setText("Test123@@")
//                handleActionLogin()

//                binding.edtInputUsername.setText("0101025405")
//                binding.edtInputPass.setText("Shb.6789")
//                handleActionLogin()
            }
        }
    }

    private fun showDialogRegister(phoneNumber: String, isNewDevice: Boolean = false) {
        val accountLogin = getUserLogin()
        val (_, encPsw) = getPassword()

        RegisterDeviceDialog(
            phoneNumber = phoneNumber, // Using username as placeholder if it's phone
            isNewDevice,
            onConfirm = {
                loginViewModel.registerDevice(accountLogin, encPsw)
            },
            onCancel = { }

        ).show(childFragmentManager, RegisterDeviceDialog.TAG)
    }

    private fun bindEdtPassword() {
        with(binding) {
            edtInputPass.filters =
                arrayOf(InputFilter.LengthFilter(50), InputFilter { source, _, _, _, _, _ ->
                    if (source != null && source.contains(" ")) "" else null
                })
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
        val accountLogin = getUserLogin()
        val (_, encPsw) = getPassword()
        postLogin(accountLogin, encPsw)
    }

    private fun getUserLogin(): String {
        return run {
            val text = binding.edtInputUsername.text?.trim().toString()
            text.ifEmpty { currentUser?.userLogin ?: "" }
        }
    }

    private fun postLogin(us: String, psW: String) {
        val params = UseCaseLogin.Params(us, psW)
        loginViewModel.checkLogin(params)
    }

    private fun getPassword(): Pair<String, String> {
        val psw = binding.edtInputPass.textValue()
        val pswEncrypt = encryptFactory.encryptRSA(plainText = psw)
        val encPsw = Base64.getEncoder().encodeToString(pswEncrypt)
        return Pair(psw, encPsw)
    }

    private fun openHomeAfterLogin(passExpireDay: Int? = null) {
        requireNavigator().open(
            destination = AppDestination.HomeArg(dayPassExpire = passExpireDay),
            clearBackStack = true,
            addToBackStack = false,
        )
        (activity as? MainActivity)?.onAuthenticatedFlowStarted()
    }

    override fun onResume() {
        hideProgressDialog()
        super.onResume()
    }

    override fun onStop() {
        hideProgressDialog()
        super.onStop()
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                loginViewModel.stateLogin.collectLatest { uiState ->
                    when (uiState) {
                        LoginUiState.Idle -> {}
                        LoginUiState.Loading -> {
                            checkShowProgressDialog()
                        }

                        is LoginUiState.Error -> {
                            hideProgressDialog()

                            when {
                                uiState.reason is LoginFailLocked -> {
                                    showDialogErrorLockUser(uiState.reason)
                                }

                                uiState.reason is LoginRegisterDevice -> {
                                    showDialogRegister(
                                        uiState.reason.masked_phone_number,
                                        uiState.reason.is_new_device
                                    )
                                }

                                uiState.reason.errorCode == ApiConst.OTP_009 -> {
                                    showErrorMessageOnly(uiState.reason.errMessage)
                                }

                                else -> {
                                    showDialogError(
                                        reason = uiState.reason
                                    )
                                }
                            }
                        }

                        is LoginUiState.Success -> {
                            hideProgressDialog()
                            // Check password expiry
                            onLoginSuccess(uiState.state.password_expire_days)
                        }
                    }
                }
            }

            launch {
                loginViewModel.stateLoading.collect {
                    if (it) checkShowProgressDialog() else hideProgressDialog()
                }
            }

            launch {
                loginViewModel.showForceUpdate.collect {
                    if (it) {
                        showDialogForceUpdate()
                    }
                }
            }

            launch {
                loginViewModel.registerDeviceResult.collect { result ->
                    hideProgressDialog()
                    val maskedPhone = result.maskedPhoneNumber ?: ""
                    transactionId = result.transactionId
                    val totalTime = result.remainingSeconds ?: result.expiresInSeconds
                    confirmDeviceView.setup(
                        phoneNumber = maskedPhone,
                        totalTime = totalTime?.times(1000L),
                        onConfirm = { otp ->
                            if (otp.isNotEmpty()) {
                                val accountLogin = getUserLogin()
                                val (_, encPsw) = getPassword()
                                transactionId?.let {
                                    loginViewModel.verifyDevice(
                                        accountLogin,
                                        encPsw,
                                        it,
                                        otp
                                    )
                                }
                            }
                            hideSoftKeyboard()
                        },
                        resendCode = {
                            // Logic to resend
                            val accountLogin = getUserLogin()
                            val (_, encPsw) = getPassword()
                            loginViewModel.registerDevice(accountLogin, encPsw)
                        },

                        onFinishCB = {
                            hideSoftKeyboard()
                        },
                        onClose = {
                            // Handle close
                        },
                        otpDefault =
                            if (BuildConfig.DEBUG)
                                result.otpCode ?: ""
                            else "",
                        title = getString(R.string.confirm_device_title)
                    )
                    if (!confirmDeviceView.isVisible) {
                        confirmDeviceView.show()
                    }
                }
            }

            launch {
                loginViewModel.verifyDeviceResult.collect { data ->
                    // Handle verify success
                    confirmDeviceView.hide()
                    // Check password expiry
                    onLoginSuccess(data.password_expire_days)
                }
            }

            launch {
                loginViewModel.verifyDeviceError.collect {
                    when {
                        codeNeedShowRedText.contains(it.errorCode) -> {
                            it.message?.let { message ->
                                confirmDeviceView.showErrorInvalidOtp(
                                    message
                                )
                            }
                        }

                        it.errorCode == ApiConst.OTP_004 || it.errorCode == ApiConst.OTP_005 -> {
                            handleErrorRegisterDevice(it)
                        }

                        else -> {
                            confirmDeviceView.hide()
                            handleErrorHome(AppReason(it.message ?: "", it.errorCode ?: ""))

                        }
                    }
                }
            }

            launch {
                loginViewModel.registerDeviceError.collect { errorData ->
                    hideProgressDialog()
                    handleErrorRegisterDevice(errorData)
                }
            }
        }
    }

    private var transactionId: String? = null

    private fun showDialogPasswordExpiring(daysRemaining: Int) {
        val message = getString(R.string.notification_password_expiring, daysRemaining)
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.notification),
            message = message,
            textPositive = getString(R.string.changePassword)
        )
    }

    private fun handleErrorRegisterDevice(errorData: RegisterDeviceData) {
        confirmDeviceView.hide()
        val message = when (errorData.errorCode) {
            ApiConst.OTP_004 -> {
                R.string.otpIncorrectly5TimesLogin
            }

            ApiConst.OTP_005 -> {
                R.string.requestOtpMore5TimesLogin
            }

            else -> R.string.otpIncorrectly5TimesLogin
        }

        CountdownBottomSheetDialog(
            message = message,
            remainingSeconds = errorData.remainingSeconds ?: 15,
            maxRequest = if (errorData.errorCode == ApiConst.OTP_004)
                errorData.maxAttempts ?: errorData.maxOtpRequestsPerWindow else
                errorData.maxOtpRequestsPerWindow ?: errorData.maxAttempts
        ).show(childFragmentManager, CountdownBottomSheetDialog.TAG)
    }

    private fun showDialogForceUpdate() {
        context?.let { ctx ->
            ForceUpdateDialog(ctx) {
                val appPackageName = ctx.packageName
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }.show()
        }
    }

    private fun showDialogErrorLockUser(reason: LoginFailLocked) {
        context?.let {
            BottomSheetDialogHelper(it).messageLoginFail(
                reason.errMessage,
                reason.lockedUntil,
                reason.countRequest
            )
        }
    }

    private fun resetInputLogin() {
        currentUser = null
        currentUserName = ""
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
            binding.tvHelloUser.text = requireContext().getTextWelcomeUser().plus(",")
            binding.tvNameUser.text = currentUserName
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun onLoginSuccess(passExpireDay: Int?) {
        when {
            passExpireDay == -1 -> {
                showErrorMessageOnly(
                    getString(R.string.passwordIsNoLongerValid)
                ) {
                    logout()
                }
            }

            (passExpireDay != null) && (passExpireDay > 0) -> {
                openHomeAfterLogin(passExpireDay)
            }

            else -> {
                openHomeAfterLogin()
            }
        }
    }

    fun updateLanguage(type: String) {
        context?.let { ct ->
            LocaleHelper.saveLanguage(ct, type)
            storage.setLanguage(type)
            LocaleHelper.setLocale(ct, type)
            requireActivity().recreate()
        }
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}
