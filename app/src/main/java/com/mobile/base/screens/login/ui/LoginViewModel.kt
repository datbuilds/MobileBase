package com.mobile.base.screens.login.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.mobile.base.BuildConfig
import com.mobile.base.base.BaseViewModel
import com.mobile.base.screens.login.state.LoginUiState
import com.mobile.base.screens.login.state.LogoutUiState
import com.mobile.base.core.core.delivery.onFailure
import com.mobile.base.core.core.delivery.onLoading
import com.mobile.base.core.core.delivery.onResultHandle
import com.mobile.base.core.core.delivery.onSuccess
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.delivery.reason.RegisterDeviceError
import com.mobile.base.core.core.delivery.reason.VerifyOtpError
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.core.core.domain.usecases.login.RegisterDeviceUseCase
import com.mobile.base.core.core.domain.usecases.login.UseCaseGetSystemVars
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogout
import com.mobile.base.core.core.domain.usecases.login.VerifyDeviceUseCase
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.data.entities.login.RegisterDeviceData
import com.mobile.base.data.entities.login.UserLog

class LoginViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseLogin: UseCaseLogin,
    private val useCaseLogout: UseCaseLogout,
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
//                    }
//                )
//            }
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
        login(param)
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
}
