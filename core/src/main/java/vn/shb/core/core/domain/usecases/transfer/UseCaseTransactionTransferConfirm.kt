package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.TransactionTransferConfirm
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseTransactionTransferConfirm(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionTransferConfirm, UseCaseTransactionTransferConfirm.Params>() {
    override suspend fun FlowCollector<ResultSHB<TransactionTransferConfirm>>.run(
        params: Params
    ) {
        emit(repoTransfer.confirmTransaction(params))
    }

    data class Params(
        val transactionId: String,
        val confirmStatus: String,
        val otp: String
    ) : UseCaseParameters

    data class BodyParams(
        val confirmStatus: String,
        val otp: String,
    ) : UseCaseParameters
}