package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.PostTransactionError
import vn.shb.core.core.delivery.reason.SendOtpTransactionError
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.source.response.AccountUserNameResponse
import vn.shb.core.core.domain.source.response.ExchangeRateModel
import vn.shb.core.core.domain.source.response.ExchangeRateResponse
import vn.shb.core.core.domain.source.response.TransactionDetailResponse
import vn.shb.core.core.domain.source.response.TransactionTransfer
import vn.shb.core.core.domain.source.response.TransactionTransferConfirm
import vn.shb.core.core.domain.source.response.TransactionTransferConfirmResponse
import vn.shb.core.core.domain.source.response.TransactionTransferResponse
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.source.response.TransferAccountResponse
import vn.shb.core.core.domain.source.response.ValidateTransactionResponse
import vn.shb.core.core.domain.source.service.ServiceTransfer
import vn.shb.core.core.domain.usecases.transfer.FundTransferRequest
import vn.shb.core.core.domain.usecases.transfer.RepositoryTransfer
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.home.TransactionDetail

class RepositoryTransferImpl(
    private val storage: AndroidSecureStorage,
    private val serviceTransfer: ServiceTransfer,
) : RepositoryTransfer, RepositoryBaseImpl() {
    override suspend fun getTransferAccount(): ResultSHB<TransferAccountData> {
        return resultGetAccountsInfo(serviceTransfer.getTransferAccount())
    }

    override suspend fun getReceiverAccount(): ResultSHB<TransferAccountData> {
        return resultGetAccountsInfo(serviceTransfer.getReceiverAccount())
    }

    override suspend fun postTransaction(request: FundTransferRequest): ResultSHB<TransactionTransfer> {
        return resultPostTransaction(serviceTransfer.postTransaction(request))
    }

    override suspend fun confirmTransaction(request: UseCaseTransactionTransferConfirm.Params): ResultSHB<TransactionTransferConfirm> {
        return resultConfirmTransaction(serviceTransfer.confirmTransaction(request))
    }

    override suspend fun getAccountByNumber(accountNumber: String): ResultSHB<AccountUserNameModel> {
        return resultAccountNumber(serviceTransfer.getAccountFromNumber(accountNumber))
    }

    override suspend fun getTransactionDetail(params: UseCaseTransactionDetail.Params): ResultSHB<TransactionDetail> {
        return resultGetTransactionDetail(serviceTransfer.getTransactionDetail(params))
    }

    override suspend fun validateTransaction(params: UseCaseValidateTransaction.Params): ResultSHB<String> {
        return resultValidateTransaction(serviceTransfer.validateTransaction(params))
    }

    override suspend fun getExchangeRates(
        sourceCurrency: String,
        targetCurrency: String
    ): ResultSHB<vn.shb.core.core.domain.source.response.ExchangeRateModel> {
        return resultExchangeRates(serviceTransfer.getExchangeRates(sourceCurrency, targetCurrency))
    }

    private fun resultExchangeRates(result: ResultSHB<ExchangeRateResponse>): ResultSHB<ExchangeRateModel> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultSHB.Success(contentResult.data ?: ExchangeRateModel())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> handleFailure(result.reason)
            else -> ResultSHB.Loading
        }
    }

    private fun resultValidateTransaction(result: ResultSHB<ValidateTransactionResponse>): ResultSHB<String> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultSHB.Success("")
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    private fun resultGetTransactionDetail(result: ResultSHB<TransactionDetailResponse>): ResultSHB<TransactionDetail> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: TransactionDetail())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    private fun resultAccountNumber(result: ResultSHB<AccountUserNameResponse>): ResultSHB<AccountUserNameModel> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: AccountUserNameModel())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    fun resultPostTransaction(result: ResultSHB<TransactionTransferResponse>): ResultSHB<TransactionTransfer> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: TransactionTransfer())
                } else {
                    ResultSHB.Failure(
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

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    private fun resultConfirmTransaction(result: ResultSHB<TransactionTransferConfirmResponse>): ResultSHB<TransactionTransferConfirm> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: TransactionTransferConfirm())
                } else {
                    ResultSHB.Failure(
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

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    private fun resultGetAccountsInfo(result: ResultSHB<TransferAccountResponse>): ResultSHB<TransferAccountData> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: TransferAccountData())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                handleFailure(result.reason)
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

}