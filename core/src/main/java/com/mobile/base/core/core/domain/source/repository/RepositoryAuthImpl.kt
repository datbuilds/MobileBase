package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.ConnectionError
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.delivery.reason.LoginFailLocked
import com.mobile.base.core.core.delivery.reason.LoginRegisterDevice
import com.mobile.base.core.core.delivery.reason.RegisterDeviceError
import com.mobile.base.core.core.delivery.reason.VerifyOtpError
import com.mobile.base.core.core.domain.source.response.LoginResponse
import com.mobile.base.core.core.domain.source.response.LogoutResponse
import com.mobile.base.core.core.domain.source.response.RegisterDeviceResponse
import com.mobile.base.core.core.domain.source.response.SystemVarData
import com.mobile.base.core.core.domain.source.response.SystemVarResponse
import com.mobile.base.core.core.domain.source.response.VerifyDeviceResponse
import com.mobile.base.core.core.domain.source.service.ServiceAuth
import com.mobile.base.core.core.domain.usecases.login.RepositoryAuth
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.data.entities.login.RegisterDeviceData
import com.mobile.base.data.entities.login.RegisterDeviceRequest
import com.mobile.base.data.entities.login.UserLog
import com.mobile.base.data.entities.login.VerifyDeviceRequest
import java.util.concurrent.TimeUnit

class RepositoryAuthImpl(
    private val storage: AndroidSecureStorage,
    private val serviceAuth: ServiceAuth,
) : RepositoryAuth {

    /**
     * logout
     */
    override suspend fun logout() =
        resultLogout(result = serviceAuth.logout())

    private fun resultLogout(result: ResultState<LogoutResponse>): ResultState<ActionDone> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: ActionDone)
                } else {
                    ResultState.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
                        )
                    )
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    override suspend fun login(param: UseCaseLogin.Params) =
        resultLogin(param, result = serviceAuth.login(params = param))

    private fun resultLogin(
        param: UseCaseLogin.Params,
        result: ResultState<LoginResponse>
    ) = when (result) {
        is ResultState.Success -> {
            val contentResult = result.successData
            if (contentResult.isSuccess()) {
                val content = contentResult.data
                if (content != null) {
                    val user = UserLog(
                        id_token = content.id_token,
                        access_token = content.access_token,
                        expires_in = content.expires_in,
                        refresh_expires_in = content.refresh_expires_in,
                        refresh_token = content.refresh_token,
                        scope = content.scope,
                        session_state = content.session_state,
                        token_type = content.token_type,
                        username = content.username,
                        userLogin = param.username,
                        title = content.title,
                        customerId = content.customerId,
                        password_expire_days = content.password_expire_days,
                    )
                    saveData(user)
                    ResultState.Success(user)
                } else {
                    resultLoginFail(contentResult)
                }
            } else {
                resultLoginFail(contentResult)
            }
        }

        is ResultState.Failure -> {
            ResultState.Failure(
                AppReason(
                    message = result.reason.errMessage,
                    code = result.reason.errorCode
                )
            )
        }

        else -> ResultState.Loading
    }

    private fun resultLoginFail(contentResult: LoginResponse): ResultState.Failure {
        return when {
            !contentResult.data?.lockedUntil.isNullOrEmpty() -> {
                ResultState.Failure(
                    LoginFailLocked(
                        message = contentResult.errorMessage,
                        lockedUntil = contentResult.data!!.lockedUntil,
                        countRequest = contentResult.data.loginFailCount.toInt(),
                    )
                )
            }

            contentResult.data?.masked_phone_number != null -> {
                ResultState.Failure(
                    LoginRegisterDevice(
                        message = contentResult.errorMessage,
                        masked_phone_number = contentResult.data.masked_phone_number!!,
                        is_new_device = contentResult.data.is_new_device,
                        contentResult.errorCode
                    )
                )
            }

            else -> {
                ResultState.Failure(
                    AppReason(
                        message = contentResult.errorMessage,
                        code = contentResult.errorCode
                    )
                )
            }
        }
    }

    private fun saveData(user: UserLog) {
        storage.apply {
            if (user.password_expire_days != -1) {
                setUserLog(user.toUserString())
            }
            setToken(user.access_token)
            setRfToken(user.refresh_token)
            updateExpireTime(TimeUnit.SECONDS.toMinutes(user.expireIn()).toInt())
            firstOpened(isFirst = true)
        }
    }

    override suspend fun refreshToken(params: UseCaseRefreshToken.Params) =
        resultRFLogin(serviceAuth.refreshToken(params = params))

    private fun resultRFLogin(result: ResultState<LoginResponse>): ResultState<UserLog> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: UserLog())
                } else {
                    resultLoginFail(contentResult)
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    override suspend fun getSystemVars(name: String) =
        resultSystemVars(serviceAuth.getSystemVars(name))

    private fun resultSystemVars(result: ResultState<SystemVarResponse>): ResultState<SystemVarData> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: SystemVarData())
                } else {
                    ResultState.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
                        )
                    )
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    override suspend fun registerDevice(
        headers: Map<String, String>,
        params: RegisterDeviceRequest
    ) = resultRegisterDevice(serviceAuth.registerDevice(headers, params))

    private fun resultRegisterDevice(result: ResultState<RegisterDeviceResponse>): ResultState<RegisterDeviceData> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: RegisterDeviceData())
                } else {
                    val content = contentResult.data
                    if (content != null) {
                        ResultState.Failure(
                            RegisterDeviceError(
                                message = contentResult.errorMessage,
                                code = contentResult.errorCode,
                                remainingSeconds = content.remainingSeconds,
                                maxAttempts = content.maxAttempts,
                                maxOtpRequestsPerWindow = content.maxOtpRequestsPerWindow,
                                transactionId = content.transactionId
                            )
                        )
                    } else {
                        ResultState.Failure(
                            AppReason(
                                message = contentResult.errorMessage,
                                code = contentResult.errorCode
                            )
                        )
                    }
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    override suspend fun verifyDevice(
        headers: Map<String, String>,
        params: VerifyDeviceRequest
    ) = resultVerifyDevice(params, serviceAuth.verifyDevice(headers, params))

    private fun resultVerifyDevice(
        params: VerifyDeviceRequest,
        result: ResultState<VerifyDeviceResponse>
    ): ResultState<UserLog> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    if (content != null) {
                        content.userLogin = params.username
                        content.username = params.username
                        saveData(content)
                        ResultState.Success(content)
                    } else {
                        ResultState.Failure(ConnectionError())
                    }
                } else {
                    ResultState.Failure(
                        VerifyOtpError(
                            contentResult.errorMessage,
                            code = contentResult.errorCode,
                            remainingSeconds = contentResult.data?.remainingSeconds,
                            maxOtpRequestsPerWindow = contentResult.data?.maxOtpRequestsPerWindow,
                            maxAttempts = contentResult.data?.maxAttempts
                        )
                    )
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }
}