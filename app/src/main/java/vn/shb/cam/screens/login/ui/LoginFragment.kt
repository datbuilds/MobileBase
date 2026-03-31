package vn.shb.cam.screens.login.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.cam.BuildConfig
import vn.shb.cam.R
import vn.shb.cam.activity.MainActivity
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.base.dialog.DialogSessionExpire
import vn.shb.cam.databinding.FragmentLoginBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.navigation.requireNavigator
import vn.shb.cam.screens.login.state.LoginUiState
import vn.shb.cam.screens.login.ui.widget.ConfirmDeviceView
import vn.shb.cam.screens.login.ui.widget.showLanguagePopup
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.checkShowProgressDialog
import vn.shb.cam.utils.extensions.clearEditTextColorFilter
import vn.shb.cam.utils.extensions.clearText
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.getTextWelcomeUser
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.hideProgressDialog
import vn.shb.cam.utils.extensions.hideSoftKeyboard
import vn.shb.cam.utils.extensions.isValidInputLogin
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.setCustomSpannable
import vn.shb.cam.utils.extensions.setOnMaterialButtonClick
import vn.shb.cam.utils.extensions.textValue
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.cam.utils.view.dialog.CountdownBottomSheetDialog
import vn.shb.cam.utils.view.dialog.ForceUpdateDialog
import vn.shb.cam.utils.view.dialog.RegisterDeviceDialog
import vn.shb.cam.utils.widgets.LocaleHelper
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.delivery.reason.LoginFailLocked
import vn.shb.core.core.delivery.reason.LoginRegisterDevice
import vn.shb.core.core.domain.usecases.login.StateLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.login.RegisterDeviceData
import vn.shb.data.entities.login.UserLog
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val loginViewModel: LoginViewModel by inject()

    private var currentUser: UserLog? = null
    var currentUserName = ""

    private var isVisiblePassword = false

    private lateinit var confirmDeviceView: ConfirmDeviceView

    private val codeNeedShowRedText = listOf(ApiConst.OTP_001, ApiConst.OTP_002, ApiConst.OTP_003)

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.tvHotline.text =
            getString(R.string.version).plus(Const.SEPARATOR_SPACE).plus(BuildConfig.VERSION_NAME)
        storage.resetToken()
//        loginViewModel.getTokenWso2()

        // Initialize embedded confirm device view
        confirmDeviceView = ConfirmDeviceView(requireContext())
        binding.flRegisterDevice.addView(confirmDeviceView)
        confirmDeviceView.visibility = View.GONE

        // Bind remembered user state before the fragment becomes visible to avoid layout jumps.
        mapUILogin()
        bindEdtPassword()
        clearFlag()

//        binding.edtInputPass.setText("12345678")
//        login()

        if (arguments?.getBoolean(AppDestination.ARG_SHOW_SESSION_EXPIRED) == true) {
            arguments?.putBoolean(AppDestination.ARG_SHOW_SESSION_EXPIRED, false)
            DialogSessionExpire().show(requireContext())
        }
    }

    private fun mapUILogin() {
        //init forgot password
        binding.tvForgotPassword.setCustomSpannable(
            getString(R.string.forgotPassword), R.color.forgotPassword,
            R.color.forgotPasswordClick
        ) {
            context?.let { ct ->
                loginViewModel.showDialogForgotPassword(
                    ct,
                    getString(R.string.passwordResetInstructions),
                    true
                )
            }
        }

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
                btnClearUsername.isVisible = !it.isNullOrEmpty()
                tvErrorUsername.isVisible = it.isNullOrEmpty()
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
                btnClearPassword.isVisible = !it.isNullOrEmpty()
                btnToggle.isVisible = !it.isNullOrEmpty()
                tvErrorPassword.isVisible = it.isNullOrEmpty()
            }

            llLanguage.setOnSingleClickListener {
                showLanguagePopup(binding.llLanguage)
            }

            if (BuildConfig.FLAVOR == "dev") {
                binding.ivLogoSHB.setOnSingleClickListener {
                    resetInputLogin()
                }
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
            onCancel = {
                showDialogVisitBranchCam(message = getString(R.string.getSupportForChanging)) {
                    context?.let { ct ->
                        loginViewModel.showDialogForgotPassword(
                            ct,
                            getString(R.string.listBranchTransactionPoint)
                        )
                    }
                }
            }
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

    private fun openHomeAfterLogin() {
        requireNavigator().open(
            destination = AppDestination.Home,
            clearBackStack = true,
            addToBackStack = false,
        )
        (activity as? MainActivity)?.onAuthenticatedFlowStarted()
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
                            if (uiState.reason is LoginFailLocked) {
                                showDialogErrorLockUser(uiState.reason)
                            } else if (uiState.reason is LoginRegisterDevice) {
                                showDialogRegister(
                                    uiState.reason.masked_phone_number,
                                    uiState.reason.is_new_device
                                )
                            } else if (
                                uiState.reason.errorCode == ApiConst.OTP_009
                            ) {
                                showDialogVisitBranchCam(message = uiState.reason.errMessage) {
                                    context?.let { ct ->
                                        loginViewModel.showDialogForgotPassword(
                                            ct,
                                            getString(R.string.listBranchTransactionPoint)
                                        )
                                    }
                                }
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

            launch {
                loginViewModel.stateLoading.collect {
                    if (it) checkShowProgressDialog() else hideProgressDialog()
                }
            }

            launch {
                loginViewModel.stateErrorWso2.collect {
                    showDialogErrorWso2(it) {
                        loginViewModel.getTokenWso2()
                    }
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
                                        accountLogin, encPsw,
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
                        }
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
                    openHomeAfterLogin()
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

    fun showDialogVisitBranchCam(
        message: String,
        onAction: (() -> Unit)? = null
    ) {
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.notification),
            message = message,
            textNegative = getString(R.string.close),
            textPositive = getString(R.string.goToBranch),
            positiveAction = {
                onAction?.invoke()
            }
        )
    }

    private fun handleErrorRegisterDevice(errorData: RegisterDeviceData) {
        confirmDeviceView.hide()
        val message = when (errorData.errorCode) {
            ApiConst.OTP_004 -> {
                R.string.otpIncorrectly5Times
            }

            ApiConst.OTP_005 -> {
                R.string.requestOtpMore5Times
            }

            else -> R.string.otpIncorrectly5Times
        }

        CountdownBottomSheetDialog(
            message = message,
            remainingSeconds = errorData.remainingSeconds ?: 15
        ).show(childFragmentManager, CountdownBottomSheetDialog.TAG)
    }

    private fun showDialogForceUpdate() {
        context?.let { ctx ->
            ForceUpdateDialog(ctx) {
//                try {
//                    val appPackageName = ctx.packageName
//                    startActivity(
//                        Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id=$appPackageName")
//                        )
//                    )
//                } catch (e: android.content.ActivityNotFoundException) {
                val appPackageName = ctx.packageName
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
//                }
            }.show()
        }
    }

    fun showDialogErrorWso2(
        reason: Reason,
        onAction: (() -> Unit)? = null
    ) {
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.notification),
            message = getString(R.string.processingError),
            textPositive = getString(R.string.tryAgain),
            positiveAction = {
                onAction?.invoke()
            },
            onDismiss = {
                loginViewModel.getTokenWso2()
            }
        )
    }

    private fun showDialogErrorLockUser(reason: LoginFailLocked) {
        context?.let {
            BottomSheetDialogHelper(it).messageLoginFail(
                reason.errMessage,
                reason.lockedUntil
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

    private fun onLoginSuccess(stateLogin: StateLogin) {
        if (stateLogin is StateLogin.OpenDashboard) {
            openHomeAfterLogin()
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
