package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseLogout(
    private val repository: RepositoryAuth
) : BaseUseCase<ActionDone, UseCaseLogout.Params>() {

    override suspend fun FlowCollector<ResultSHB<ActionDone>>.run(params: Params) {
        emit(repository.logout(params))
    }

    data class Params(
        val type: String = "ALL",
    ) : UseCaseParameters
}
