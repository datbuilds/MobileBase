package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountDetailsData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters


class UseCaseAccountDetails(private val repository: RepositoryUser) :
    BaseUseCase<AccountDetailsData, UseCaseAccountDetails.Params>() {
    override suspend fun FlowCollector<ResultSHB<AccountDetailsData>>.run(
        params: Params
    ) {
        emit(repository.getAccountDetails(params))
    }


    data class Params(val accountNumber: String) : UseCaseParameters
}