package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.TransactionTransfer
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseTransactionTransfer(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionTransfer, FundTransferRequest>() {
    override suspend fun FlowCollector<ResultSHB<TransactionTransfer>>.run(
        params: FundTransferRequest
    ) {
       emit((repoTransfer.postTransaction(params)))
    }
}