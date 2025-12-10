package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.domain.source.api.ApiTransfer
import vn.shb.core.core.domain.usecases.transfer.FundTransferRequest
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.core.retrofit.SafeExecute

class ServiceTransfer(private val api: ApiTransfer) : SafeExecute() {

    suspend fun getTransferAccount() = execute {
        api.getTransferAccount().awaitResponse()
    }

    suspend fun getReceiverAccount() = execute {
        api.getReceiverAccount().awaitResponse()
    }

    suspend fun postTransaction(request: FundTransferRequest) = execute {
        api.postTransaction(request).awaitResponse()
    }

    suspend fun confirmTransaction(request: UseCaseTransactionTransferConfirm.Params) = execute {
        api.confirmTransaction(
            request.transactionId,
            UseCaseTransactionTransferConfirm.BodyParams(request.confirmStatus, request.otp)
        )
    }

    suspend fun getAccountFromNumber(accountNumber: String) = execute {
        api.getAccountByNumber(accountNumber)
    }

    suspend fun getTransactionDetail(params: UseCaseTransactionDetail.Params) = execute {
        api.getTransactionDetail(
            params.refNo,
            params.acctNo,
            params.drCrFlg
        )
    }

    suspend fun validateTransaction(params: UseCaseValidateTransaction.Params) = execute {
        api.validateTransfer(params)
    }
}