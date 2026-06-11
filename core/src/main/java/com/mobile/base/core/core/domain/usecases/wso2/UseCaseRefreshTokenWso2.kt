package com.mobile.base.core.core.domain.usecases.wso2

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.wso2.WsoData

class UseCaseRefreshTokenWso2(
    private val repository: RepositoryWSO
) : BaseUseCase<WsoData, UseCaseRefreshTokenWso2.InputParams>() {

    override suspend fun FlowCollector<ResultState<WsoData>>.run(params: InputParams) {
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
