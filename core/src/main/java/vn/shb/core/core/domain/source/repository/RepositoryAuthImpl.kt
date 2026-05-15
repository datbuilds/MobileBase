package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ConnectionError
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.delivery.reason.LoginFailLocked
import vn.shb.core.core.delivery.reason.LoginRegisterDevice
import vn.shb.core.core.delivery.reason.RegisterDeviceError
import vn.shb.core.core.delivery.reason.VerifyOtpError
import vn.shb.core.core.domain.source.response.LoginResponse
import vn.shb.core.core.domain.source.response.LogoutResponse
import vn.shb.core.core.domain.source.response.RegisterDeviceResponse
import vn.shb.core.core.domain.source.response.SystemVarData
import vn.shb.core.core.domain.source.response.SystemVarResponse
import vn.shb.core.core.domain.source.response.VerifyDeviceResponse
import vn.shb.core.core.domain.source.service.ServiceAuth
import vn.shb.core.core.domain.usecases.login.RepositoryAuth
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.RegisterDeviceData
import vn.shb.data.entities.login.RegisterDeviceRequest
import vn.shb.data.entities.login.UserLog
import vn.shb.data.entities.login.VerifyDeviceRequest
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

    private fun resultLogout(result: ResultSHB<LogoutResponse>): ResultSHB<ActionDone> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: ActionDone)
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
                        )
                    )
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    override suspend fun login(param: UseCaseLogin.Params) =
        resultLogin(param, result = serviceAuth.login(params = param))

    private fun resultLogin(
        param: UseCaseLogin.Params,
        result: ResultSHB<LoginResponse>
    ) = when (result) {
        is ResultSHB.Success -> {
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
                    ResultSHB.Success(user)
                } else {
                    resultLoginFail(contentResult)
                }
            } else {
                resultLoginFail(contentResult)
            }
        }

        is ResultSHB.Failure -> {
            ResultSHB.Failure(
                AppReason(
                    message = result.reason.errMessage,
                    code = result.reason.errorCode
                )
            )
        }

        else -> ResultSHB.Loading
    }

    private fun resultLoginFail(contentResult: LoginResponse): ResultSHB.Failure {
        return when {
            !contentResult.data?.lockedUntil.isNullOrEmpty() -> {
                ResultSHB.Failure(
                    LoginFailLocked(
                        message = contentResult.errorMessage,
                        lockedUntil = contentResult.data!!.lockedUntil,
                        countRequest = contentResult.data.loginFailCount.toInt(),
                    )
                )
            }

            contentResult.data?.masked_phone_number != null -> {
                ResultSHB.Failure(
                    LoginRegisterDevice(
                        message = contentResult.errorMessage,
                        masked_phone_number = contentResult.data.masked_phone_number!!,
                        is_new_device = contentResult.data.is_new_device,
                        contentResult.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Failure(
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

    private fun resultRFLogin(result: ResultSHB<LoginResponse>): ResultSHB<UserLog> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: UserLog())
                } else {
                    resultLoginFail(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    override suspend fun getSystemVars(name: String) =
        resultSystemVars(serviceAuth.getSystemVars(name))

    private fun resultSystemVars(result: ResultSHB<SystemVarResponse>): ResultSHB<SystemVarData> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: SystemVarData())
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
                        )
                    )
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    override suspend fun registerDevice(
        headers: Map<String, String>,
        params: RegisterDeviceRequest
    ) = resultRegisterDevice(serviceAuth.registerDevice(headers, params))

    private fun resultRegisterDevice(result: ResultSHB<RegisterDeviceResponse>): ResultSHB<RegisterDeviceData> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: RegisterDeviceData())
                } else {
                    val content = contentResult.data
                    if (content != null) {
                        ResultSHB.Failure(
                            RegisterDeviceError(
                                message = contentResult.errorMessage ?: content.message
                                ?: "Unknown Error",
                                code = contentResult.errorCode ?: "",
                                remainingSeconds = content.remainingSeconds,
                                maxAttempts = content.maxAttempts,
                                maxOtpRequestsPerWindow = content.maxOtpRequestsPerWindow,
                                transactionId = content.transactionId
                            )
                        )
                    } else {
                        ResultSHB.Failure(
                            AppReason(
                                message = contentResult.errorMessage ?: "Unknown Error",
                                code = contentResult.errorCode ?: ""
                            )
                        )
                    }
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    override suspend fun verifyDevice(
        headers: Map<String, String>,
        params: VerifyDeviceRequest
    ) = resultVerifyDevice(params, serviceAuth.verifyDevice(headers, params))

    private fun resultVerifyDevice(
        params: VerifyDeviceRequest,
        result: ResultSHB<VerifyDeviceResponse>
    ): ResultSHB<UserLog> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    if (content != null) {
                        content.userLogin = params.username
                        content.username = params.username
                        saveData(content)
                        ResultSHB.Success(content)
                    } else {
                        ResultSHB.Failure(ConnectionError())
                    }
                } else {
                    ResultSHB.Failure(
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

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }
}