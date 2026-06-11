package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.domain.source.request.ChangePasswordRequest
import com.mobile.base.core.core.domain.source.response.AccountData
import com.mobile.base.core.core.domain.source.response.AccountDetailsData
import com.mobile.base.core.core.domain.source.response.AccountDetailsResponse
import com.mobile.base.core.core.domain.source.response.AccountsInfoResponse
import com.mobile.base.core.core.domain.source.response.DefaultAccountResponse
import com.mobile.base.core.core.domain.source.response.TransactionData
import com.mobile.base.core.core.domain.source.response.TransactionResponse
import com.mobile.base.core.core.domain.source.response.UserInfoResponse
import com.mobile.base.core.core.domain.source.service.ServiceUser
import com.mobile.base.core.core.domain.usecases.home.RepositoryUser
import com.mobile.base.core.core.domain.usecases.home.UseCaseAccountDetails
import com.mobile.base.core.core.domain.usecases.home.UseCaseTransaction
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.data.entities.home.UserInfo

class RepositoryUserImpl(
    private val storage: AndroidSecureStorage,
    private val serviceUser: ServiceUser,
) : RepositoryUser {

    override suspend fun getUserInfo(): ResultState<UserInfo> {
        return resultGetUserInfo(result = serviceUser.getUserInfo())
    }

    private fun resultGetUserInfo(result: ResultState<UserInfoResponse>): ResultState<UserInfo> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: UserInfo())
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

    override suspend fun getAccountsInfos(): ResultState<AccountData> {
        return resultGetAccountsInfo(result = serviceUser.getAccountsInfo())
    }

    private fun resultGetAccountsInfo(result: ResultState<AccountsInfoResponse>): ResultState<AccountData> {
//        return ResultState.Success( AccountData())
       return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: AccountData())
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

    override suspend fun getAccountDetails(params: UseCaseAccountDetails.Params): ResultState<AccountDetailsData> {
        return resultGetAccountsDetails(result = serviceUser.getAccountDetails(params))
    }

    private fun resultGetAccountsDetails(result: ResultState<AccountDetailsResponse>): ResultState<AccountDetailsData> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(contentResult.data ?: AccountDetailsData())
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


    override suspend fun getTransactions(params: UseCaseTransaction.Params): ResultState<TransactionData> {
        return resultGetTransactions(result = serviceUser.getTransactions(params))
    }

    private fun resultGetTransactions(result: ResultState<TransactionResponse>): ResultState<TransactionData> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(contentResult.data ?: TransactionData())
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

    override suspend fun setDefaultAccount(accountNo: String): ResultState<Boolean> {
        return resultSetDefaultAccount(result = serviceUser.setDefaultAccount(accountNo))
    }

    private fun resultSetDefaultAccount(result: ResultState<DefaultAccountResponse>): ResultState<Boolean> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(true)
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

    override suspend fun changePassword(request: ChangePasswordRequest): ResultState<com.mobile.base.core.core.domain.source.response.ChangePasswordResponse> {
        return resultChangePassword(result = serviceUser.changePassword(request))
    }

    private fun resultChangePassword(result: ResultState<com.mobile.base.core.core.domain.source.response.ChangePasswordResponse>): ResultState<com.mobile.base.core.core.domain.source.response.ChangePasswordResponse> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(contentResult)
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
}