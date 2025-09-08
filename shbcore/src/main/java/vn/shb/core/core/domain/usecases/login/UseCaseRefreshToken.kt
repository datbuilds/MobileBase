package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.login.UserInfo

class UseCaseRefreshToken(
    private val repository: RepositoryAuth
) : BaseUseCase<UserInfo, UseCaseRefreshToken.Params>() {

    override suspend fun FlowCollector<ResultSHB<UserInfo>>.run(params: Params) {
        emit(repository.refreshToken(params))
    }

    data class Params(val refreshToken: String = "") : UseCaseParameters
}
