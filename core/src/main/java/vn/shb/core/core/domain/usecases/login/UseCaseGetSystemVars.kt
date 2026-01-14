package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.SystemVarData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseGetSystemVars(private val repository: RepositoryAuth) :
    BaseUseCase<SystemVarData, UseCaseGetSystemVars.Params>() {

    override suspend fun FlowCollector<ResultSHB<SystemVarData>>.run(params: Params) {
        emit(repository.getSystemVars(params.name))
    }

    data class Params(
        val name: String = ""
    ) : UseCaseParameters
}
