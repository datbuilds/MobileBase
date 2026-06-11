package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.reason.PostTransactionError
import com.mobile.base.core.core.delivery.reason.SendOtpTransactionError
import com.mobile.base.core.core.domain.source.response.AccountUserNameModel
import com.mobile.base.core.core.domain.source.response.AccountUserNameResponse
import com.mobile.base.core.core.domain.source.response.ExchangeRateModel
import com.mobile.base.core.core.domain.source.response.ExchangeRateResponse
import com.mobile.base.core.core.domain.source.response.TransactionDetailResponse
import com.mobile.base.core.core.domain.source.response.TransactionTransfer
import com.mobile.base.core.core.domain.source.response.TransactionTransferConfirm
import com.mobile.base.core.core.domain.source.response.TransactionTransferConfirmResponse
import com.mobile.base.core.core.domain.source.response.TransactionTransferResponse
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.core.core.domain.source.response.TransferAccountResponse
import com.mobile.base.core.core.domain.source.response.ValidateTransactionResponse
import com.mobile.base.core.core.domain.source.service.ServiceTransfer
import com.mobile.base.core.core.domain.usecases.transfer.FundTransferRequest
import com.mobile.base.core.core.domain.usecases.transfer.RepositoryTransfer
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.data.entities.home.TransactionDetail

class RepositoryTransferImpl(
    private val storage: AndroidSecureStorage,
    private val serviceTransfer: ServiceTransfer,
) : RepositoryTransfer, RepositoryBaseImpl() {
    override suspend fun getTransferAccount(): ResultState<TransferAccountData> {
        return resultGetAccountsInfo(serviceTransfer.getTransferAccount())
    }

    override suspend fun getReceiverAccount(): ResultState<TransferAccountData> {
        return resultGetAccountsInfo(serviceTransfer.getReceiverAccount())
    }

    override suspend fun postTransaction(request: FundTransferRequest): ResultState<TransactionTransfer> {
        return resultPostTransaction(serviceTransfer.postTransaction(request))
    }

    override suspend fun confirmTransaction(request: UseCaseTransactionTransferConfirm.Params): ResultState<TransactionTransferConfirm> {
        return resultConfirmTransaction(serviceTransfer.confirmTransaction(request))
    }

    override suspend fun getAccountByNumber(accountNumber: String): ResultState<AccountUserNameModel> {
        return resultAccountNumber(serviceTransfer.getAccountFromNumber(accountNumber))
    }

    override suspend fun getTransactionDetail(params: UseCaseTransactionDetail.Params): ResultState<TransactionDetail> {
        return resultGetTransactionDetail(serviceTransfer.getTransactionDetail(params))
    }

    override suspend fun validateTransaction(params: UseCaseValidateTransaction.Params): ResultState<String> {
        return resultValidateTransaction(serviceTransfer.validateTransaction(params))
    }

    override suspend fun getExchangeRates(
        sourceCurrency: String,
        targetCurrency: String
    ): ResultState<com.mobile.base.core.core.domain.source.response.ExchangeRateModel> {
        return resultExchangeRates(serviceTransfer.getExchangeRates(sourceCurrency, targetCurrency))
    }

    private fun resultExchangeRates(result: ResultState<ExchangeRateResponse>): ResultState<ExchangeRateModel> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(contentResult.data ?: ExchangeRateModel())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> handleFailure(result.reason)
            else -> ResultState.Loading
        }
    }

    private fun resultValidateTransaction(result: ResultState<ValidateTransactionResponse>): ResultState<String> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success("")
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    private fun resultGetTransactionDetail(result: ResultState<TransactionDetailResponse>): ResultState<TransactionDetail> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: TransactionDetail())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    private fun resultAccountNumber(result: ResultState<AccountUserNameResponse>): ResultState<AccountUserNameModel> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: AccountUserNameModel())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    fun resultPostTransaction(result: ResultState<TransactionTransferResponse>): ResultState<TransactionTransfer> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: TransactionTransfer())
                } else {
                    ResultState.Failure(
                        PostTransactionError(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode,
                            maxOtpRequestsPerWindow = contentResult.data?.maxOtpRequestsPerWindow,
                            remainingSeconds = contentResult.data?.remainingSeconds,
                            maxAttempts = contentResult.data?.maxAttempts,
                            success = contentResult.data?.success,
                        )
                    )
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    private fun resultConfirmTransaction(result: ResultState<TransactionTransferConfirmResponse>): ResultState<TransactionTransferConfirm> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: TransactionTransferConfirm())
                } else {
                    ResultState.Failure(
                        SendOtpTransactionError(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode,
                            transactionId = contentResult.data?.transactionId.toString(),
                            isValid = contentResult.data?.isValid,
                            remainingAttempts = contentResult.data?.remainingAttempts,
                            remainingSeconds = contentResult.data?.remainingSeconds,
                            maxAttempts = contentResult.data?.maxAttempts,

                            )
                    )
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

    private fun resultGetAccountsInfo(result: ResultState<TransferAccountResponse>): ResultState<TransferAccountData> {
        return when (result) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultState.Success(content ?: TransferAccountData())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultState.Loading
            }
        }
    }

}