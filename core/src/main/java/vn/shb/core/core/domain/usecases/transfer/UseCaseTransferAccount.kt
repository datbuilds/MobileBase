package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseTransferAccount(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransferAccountData, UseCaseTransferAccount.Params>() {
    override suspend fun FlowCollector<ResultSHB<TransferAccountData>>.run(
        params: Params
    ) {
        if (params.isTransferAccount) {
            emit(repoTransfer.getTransferAccount())
        } else {
            emit(repoTransfer.getReceiverAccount())
        }
    }

    data class Params(
        val isTransferAccount: Boolean
    ) : UseCaseParameters
}