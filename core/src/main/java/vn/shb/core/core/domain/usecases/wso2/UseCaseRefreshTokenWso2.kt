package vn.shb.core.core.domain.usecases.wso2

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.wso2.WsoData

class UseCaseRefreshTokenWso2(
    private val repository: RepositoryWSO
) : BaseUseCase<WsoData, UseCaseRefreshTokenWso2.InputParams>() {

    override suspend fun FlowCollector<ResultSHB<WsoData>>.run(params: InputParams) {
        emit(repository.refreshToken(params.token, params.params))
    }

    data class Params(
        val grant_type: String,
        val refresh_token: String,
    )

    data class InputParams(
        val token: String,
        val params: Params
    ) : UseCaseParameters
}
