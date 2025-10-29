package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.home.TransactionDetail

class UseCaseTransactionDetail(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionDetail, UseCaseTransactionDetail.Params>() {
    override suspend fun FlowCollector<ResultSHB<TransactionDetail>>.run(
        params: Params
    ) {
        emit(repoTransfer.getTransactionDetail(params))
    }

    data class Params(
        val refNo: String,
        val acctNo: String,
        val drCrFlg: String,
    ) : UseCaseParameters
}