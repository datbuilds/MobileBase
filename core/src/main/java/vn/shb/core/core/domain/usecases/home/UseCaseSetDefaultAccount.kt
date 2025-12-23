package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseSetDefaultAccount(private val repository: RepositoryUser) :
    BaseUseCase<Boolean, UseCaseSetDefaultAccount.Params>() {

    override suspend fun FlowCollector<ResultSHB<Boolean>>.run(params: Params) {
        emit(repository.setDefaultAccount(params.accountNo))
    }

    data class Params(val accountNo: String) : UseCaseParameters
}
