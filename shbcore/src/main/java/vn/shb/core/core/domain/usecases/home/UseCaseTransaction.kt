package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.source.response.AccountDetailsData
import vn.shb.core.core.domain.source.response.TransactionData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseTransaction(private val repository: RepositoryUser) : BaseUseCase<TransactionData, UseCaseTransaction.Params>() {

    override suspend fun FlowCollector<ResultSHB<TransactionData>>.run(
        params: Params
    ) {
        emit(repository.getTransactions(params))
    }

    data class Params(
        val accountNumber: String? = null,
        val queryType : String? = null,
        val fromDate: String? = null,
        val toDate: String? = null,
    ) : UseCaseParameters
}