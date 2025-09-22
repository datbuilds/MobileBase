package vn.shb.lao.screens.login.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.domain.usecases.login.StateLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.login.UserConverters
import vn.shb.data.entities.login.UserInfo
import vn.shb.lao.R
import vn.shb.lao.activity.dashboard.DashboardActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentLoginBinding
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.utils.extensions.clearEditTextColorFilter
import vn.shb.lao.utils.extensions.clearText
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.nextActivity
import vn.shb.lao.utils.extensions.setErrorAndBackground
import vn.shb.lao.utils.extensions.setErrorAndBackgroundDefault
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.extensions.textValue
import java.util.Base64

class LoginFragment : BaseFragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val encryptFactory: EncryptManager by inject()
    private val storage: AndroidSecureStorage by inject()
    private val viewModel: LoginViewModel by inject()
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()

    private var currentUser: UserInfo? = null
    private var currentUserName = ""

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean -> }

    override fun initView(view: View) {
        setSingleView()
        mapUILogin()
    }

    private fun setSingleView() {
        binding.tvForgotPassword.paintFlags = binding.tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
//        binding.tvVersion.text = "Phiên bản ${BuildConfig.VERSION_NAME}"
    }

    private fun mapUILogin() {
        // set background login
        val bgLogin = storage.getBackgroundLogin()
        val bgBitmap: Bitmap? =
            if (bgLogin.isNotEmpty()) {
                prepareBGLogin(bgLogin)
            } else {
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.img_bg_login_res)
                if (drawable is BitmapDrawable) drawable.bitmap else drawable?.toBitmap()
            }

        // set user info
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

    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    binding.coordinatorLayout,
                    "Sale App cần cấp quyền thông báo trong ứng dụng",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Cài đặt") {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri: Uri =
                            Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .show()
            }

            else -> {
                // The registered ActivityResultCallback gets the result of this request
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(binding) {
//            containerLogin.setOnTouchListener { _, _ ->
//                hideSoftKeyboard(0)
//                false
//            }
//
//            userNameView.onListener(
//                object : OnUserInputListener {
//                    override fun onLogin() {
//                        login()
//                    }
//                }
//            )
            edtInputUsername.setOnFocusChangeListener{ _, hasFocus ->
                inputUserNameLayout.isSelected = hasFocus
            }
            edtInputPass.setOnFocusChangeListener{ _, hasFocus ->
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
                showDialogError(
                    message =
                        "Vui lòng liên hệ tới bộ phận IT Support để được hỗ trợ cấp lại mật khẩu đăng nhập"
                )
            }

            btnLogin.setOnSingleClickListener { nextDashboard() }
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
//            userNameView.setData(userName = "")
//            userNameView.clearEditTextColorFilter()
            inputPasswordLayout.clearText()
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun prepareViewUserLogged(user: UserInfo) {
        currentUser = user
        currentUserName = user.username

        binding.apply {
//            userNameView.setData(userName = currentUserName)
            inputPasswordLayout.clearEditTextColorFilter()
        }
    }

    private fun comingSoon() {
        showDialogError(
            title = "Thông báo",
            message =
                "Tính năng này hiện đang được hoàn thiện và sẽ sớm ra mắt trong thời gian tới!",
        )
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
