package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseLogin(private val repository: RepositoryAuth) :
    BaseUseCase<StateLogin, UseCaseLogin.Params>() {

    override suspend fun FlowCollector<ResultSHB<StateLogin>>.run(params: Params) {
        emit(repository.login(params))
    }

    data class  Params(
        val username: String = "",
        val password: String = ""
    ) : UseCaseParameters
}
