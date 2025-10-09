package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.source.response.UserInfoResponse
import vn.shb.core.core.domain.source.service.ServiceUser
import vn.shb.core.core.domain.usecases.home.RepositoryUser
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserInfo

class RepositoryUserImpl(
    private val storage: AndroidSecureStorage,
    private val serviceUser: ServiceUser,
) : RepositoryUser {
    override suspend fun getUserInfo(): ResultSHB<UserInfo> {
        return resultGetUserInfo(result = serviceUser.getUserInfo())
    }

    private fun resultGetUserInfo(result: ResultSHB<UserInfoResponse>): ResultSHB<UserInfo> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: UserInfo())
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
}