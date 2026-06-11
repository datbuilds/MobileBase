package vn.shb.cam.screens.login.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.shb.cam.BuildConfig
import vn.shb.cam.R
import vn.shb.cam.base.BaseViewModel
import vn.shb.cam.databinding.LayoutBranchListBinding
import vn.shb.cam.screens.login.helper.BranchAdapter
import vn.shb.cam.screens.login.model.Branch
import vn.shb.cam.screens.login.state.LoginUiState
import vn.shb.cam.screens.login.state.LogoutUiState
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.delivery.reason.RegisterDeviceError
import vn.shb.core.core.delivery.reason.VerifyOtpError
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.login.RegisterDeviceUseCase
import vn.shb.core.core.domain.usecases.login.UseCaseGetSystemVars
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.VerifyDeviceUseCase
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.RegisterDeviceData
import vn.shb.data.entities.login.UserLog
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseLogin: UseCaseLogin,
    private val useCaseLogout: UseCaseLogout,
    private val useCaseGetTokenWso2: UseCaseGetTokenWso2,
    private val useCaseGetSystemVars: UseCaseGetSystemVars,
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val verifyDeviceUseCase: VerifyDeviceUseCase,
) : BaseViewModel() {

    private val _registerDeviceResult = Channel<RegisterDeviceData>(Channel.BUFFERED)
    val registerDeviceResult = _registerDeviceResult.receiveAsFlow()
    private val _registerDeviceError = Channel<RegisterDeviceData>(Channel.BUFFERED)
    val registerDeviceError = _registerDeviceError.receiveAsFlow()

    private val _verifyDeviceResult = Channel<UserLog>(Channel.BUFFERED)
    val verifyDeviceResult = _verifyDeviceResult.receiveAsFlow()
    private val _verifyDeviceError = Channel<RegisterDeviceData>(Channel.BUFFERED)
    val verifyDeviceError = _verifyDeviceError.receiveAsFlow()

    private val _state = MutableSharedFlow<LoginUiState>()
    val stateLogin: SharedFlow<LoginUiState> = _state
    private val _stateErrorWso2 = Channel<Reason>(Channel.BUFFERED)
    val stateErrorWso2 = _stateErrorWso2.receiveAsFlow()

    private val _stateLogout = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val stateLogout = _stateLogout.asStateFlow()

    private val _showForceUpdate = Channel<Boolean>(Channel.BUFFERED)
    val showForceUpdate = _showForceUpdate.receiveAsFlow()

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading = _stateLoading.asStateFlow()

    suspend fun stateLoading(isLoading: Boolean) {
        _stateLoading.emit(isLoading)
    }

    private suspend fun stateLogin(state: LoginUiState) {
        _state.emit(state)
    }

    fun checkSystemVars() {
        viewModelScope.launch {
//            useCaseGetSystemVars(UseCaseGetSystemVars.Params("MBA")).collect {
//                // Handle result if needed, for now just logging or silent failure as per request (user just asked to call it)
//                // If specific logic is needed on success, we can add it here.
//                it.onResultHandle(
//                    loadingBlock = {
//                        stateLoading(true)
//                    },
//                    successBlock = { data ->
            stateLoading(false)
//                        if (data.isNeedUpdate(BuildConfig.VERSION_NAME)) {
//                            _showForceUpdate.send(true)
//                        }
//                    },
//                    failureBlock = {
//                        stateLoading(false)
//                        handleErrorTokenWso2(it)
//                    }
//                )
//            }
        }
    }

    fun isExpireTokenWso2(call: () -> Unit) {
        val oldTime = storage.getTimeGetTokenWso2()
        val currentTime = System.currentTimeMillis()
//        Log.i("3242343242343", "${currentTime - oldTime}")
//        Log.i("3242343242343", "${(storage.getExpireTime() - 1) * 60 * 1000}")
        val isExpire = (currentTime - oldTime) > (storage.getExpireTime() - 1) * 60 * 1000
        if (isExpire) {
            getTokenWso2 {
                call()
            }
        } else {
            call()
        }
    }

    fun registerDevice(username: String, password: String) {
        viewModelScope.launch {
            val headers = mapOf(
                "X-Device-Model" to android.os.Build.MODEL,
                "X-OS-Type" to "Android",
                "X-OS-Version" to android.os.Build.VERSION.RELEASE,
                "X-App-Version" to BuildConfig.VERSION_NAME,
            )
            val params = RegisterDeviceUseCase.Params(
                headers = headers,
                username = username,
                password = password
            )

            registerDeviceUseCase(params).collect {
                it.onLoading {
                    stateLoading(true)
                }
                it.onFailure { error ->
                    stateLoading(false)
                    if (error is RegisterDeviceError) {
                        val data = RegisterDeviceData(
                            transactionId = error.transactionId,
                            message = error.message,
                            remainingSeconds = error.remainingSeconds,
                            maxAttempts = error.maxAttempts,
                            maxOtpRequestsPerWindow = error.maxOtpRequestsPerWindow,
                            expiresInSeconds = null, // or pass if Reason has it? Reason only has remainingSeconds currently.
                            maskedPhoneNumber = null, // Reason doesn't have it currently
                            errorCode = error.errorCode
                        )
                        _registerDeviceError.send(data)
                    } else {
                        stateLogin(LoginUiState.Error(error))
                    }
                }
                it.onSuccess {
                    _registerDeviceResult.send(it)
                    stateLoading(false)
                }
            }
        }
    }

    fun verifyDevice(username: String, password: String, transactionId: String, otpCode: String) {
        viewModelScope.launch {
            val headers = mapOf(
                "X-Device-Model" to android.os.Build.MODEL,
                "X-OS-Type" to "Android",
                "X-OS-Version" to android.os.Build.VERSION.RELEASE,
                "X-App-Version" to BuildConfig.VERSION_NAME,
            )
            val params = VerifyDeviceUseCase.Params(
                headers = headers,
                username = username,
                password = password,
                transactionId = transactionId,
                otpCode = otpCode
            )

            verifyDeviceUseCase(params).collect {
                it.onLoading {
                    stateLoading(true)
                }
                it.onFailure { error ->
                    stateLoading(false)
                    if (error is VerifyOtpError) {
                        val data = RegisterDeviceData(
                            message = error.message,
                            errorCode = error.errorCode,
                            remainingSeconds = error.remainingSeconds,
                            maxAttempts = error.maxAttempts,
                            maxOtpRequestsPerWindow = error.maxOtpRequestsPerWindow
                        )
                        _verifyDeviceError.send(data)
                    } else {
                        _state.emit(LoginUiState.Error(error))
                    }
                }
                it.onSuccess { data ->
                    _verifyDeviceResult.send(data)
                    stateLoading(false)
                }
            }
        }
    }

    fun checkLogin(param: UseCaseLogin.Params) {
        if (storage.getTokenWso2().isEmpty()) {
            getTokenWso2BackUpLogin(param)
        } else {
            isExpireTokenWso2 {
                login(param)
            }
        }
    }

    fun login(param: UseCaseLogin.Params) {
        viewModelScope.launch {
            useCaseLogin(param).collect {
                it.onResultHandle(
                    loadingBlock = {
                        stateLogin(LoginUiState.Loading)
                    },
                    failureBlock = { reason ->
                        stateLogin(LoginUiState.Error(reason))
                    },
                    successBlock = { state ->
                        stateLogin(LoginUiState.Success(state))
                    })
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            useCaseLogout(None).collect {
                it.onResultHandle(loadingBlock = {
                    _stateLogout.value = LogoutUiState.Loading
                }, failureBlock = { reason ->
                    _stateLogout.value = LogoutUiState.Error(reason as AppReason)
                }, successBlock = { state ->
                    _stateLogout.value = LogoutUiState.Success(state)
                })
            }
        }
    }

    fun getTokenWso2(success: (() -> Unit)? = null) {
        viewModelScope.launch {
            val paramsWso2 = UseCaseGetTokenWso2.InputParams(
                BuildConfig.AUTHORIZATION, UseCaseGetTokenWso2.Params(
                    grant_type = BuildConfig.GRANT_TYPE,
                    username = BuildConfig.USERNAME,
                    password = BuildConfig.PASSWORD,
                    scope = storage.getDeviceId(),
                )
            )
            useCaseGetTokenWso2(paramsWso2).collect { result ->
                result.onSuccess { trans ->
                    storage.setTokenWso2(trans.access_token)
                    storage.setRfTokenWso2(trans.refresh_token)
                    storage.updateExpireTime(
                        TimeUnit.SECONDS.toMinutes(trans.expireIn()).toInt()
                    )
                    delay(300)
                    success?.invoke()
//                    checkSystemVars()
                }
                result.onFailure { error ->
                    stateLoading(false)
                    handleErrorTokenWso2(error)
                }
                result.onLoading {
                    stateLoading(true)
//                    _state.value = LoginUiState.Loading
                }
            }
        }
    }

    fun getTokenWso2BackUpLogin(param: UseCaseLogin.Params) {
        viewModelScope.launch {
            val paramsWso2 = UseCaseGetTokenWso2.InputParams(
                BuildConfig.AUTHORIZATION, UseCaseGetTokenWso2.Params(
                    grant_type = BuildConfig.GRANT_TYPE,
                    username = BuildConfig.USERNAME,
                    password = BuildConfig.PASSWORD,
                    scope = storage.getDeviceId(),
                )
            )
            useCaseGetTokenWso2(paramsWso2).collect { result ->
                result.onSuccess { trans ->
                    storage.setTokenWso2(trans.access_token)
                    storage.setRfTokenWso2(trans.refresh_token)
                    storage.updateExpireTime(
                        TimeUnit.SECONDS.toMinutes(trans.expireIn()).toInt()
                    )
                    delay(200)
                    login(param)
                }
                result.onFailure { error ->
                    stateLoading(false)
                    stateLogin(LoginUiState.Error(error))
                }
                result.onLoading {
                    stateLoading(true)
                }
            }
        }
    }

    private fun handleErrorTokenWso2(error: Reason) {
        viewModelScope.launch {
            _stateErrorWso2.send(error)
        }
    }

    fun showDialogForgotPassword(context: Context, title: String, isShowNote: Boolean = false) {
        context.apply {

            val bindingSup = LayoutBranchListBinding.inflate(LayoutInflater.from(this))
            bindingSup.tvOrangeMessage.isVisible = isShowNote

            //mock data
            val branches = getListAddress(context)

            bindingSup.rvBranches.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BranchAdapter(branches) { typeClick, branch ->
                    if (typeClick == BranchAdapter.CLICK_HOTLINE) {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:${branch.tel}".toUri())
                        context.startActivity(intent)
                    } else {
                        openMap(context, branch.latitude, branch.longitude, branch.address)
                    }
                }
            }
            BottomSheetDialogHelper(context).message(
                title = title,
                supView = bindingSup.root,
                isClose = true
            )
        }
    }

    fun openMap(context: Context, latitude: String, longitude: String, placeName: String) {
        val uri = "$latitude,$longitude"

        // Thử mở Google Maps trước
        val gmmIntentUri = "geo:$uri?q=$uri($placeName)".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
            return
        }

        // Fallback: mở app bản đồ mặc định
        val fallbackUri = "geo:$uri?q=$uri($placeName)".toUri()
        val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)

        context.startActivity(fallbackIntent)
    }

    private fun getListAddress(context: Context): List<Branch> {
        return listOf(
            Branch(
                context.getString(R.string.shbBranch1),
                context.getString(R.string.shbBranch1Address),
                "0 23 221 900",
                "11.5602485",
                "104.9267921"
            ),
            Branch(
                context.getString(R.string.shbBranch2),
                context.getString(R.string.shbBranch2Address),
                "0 23 882 358",
                "11.561437",
                "104.907504"
            ),
            Branch(
                context.getString(R.string.shbBranch3),
                context.getString(R.string.shbBranch3Address),
                "0 23 890 353",
                "11.5316183",
                "104.9491833"
            ),
            Branch(
                context.getString(R.string.shbBranch4),
                context.getString(R.string.shbBranch4Address),
                "023 880091",
                "11.589961",
                "104.874072"
            )
        )
    }
}
