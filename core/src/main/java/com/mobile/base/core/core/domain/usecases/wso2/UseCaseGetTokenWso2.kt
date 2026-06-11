package com.mobile.base.core.core.domain.usecases.wso2

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.wso2.WsoData

class UseCaseGetTokenWso2(
    private val repository: RepositoryWSO
) : BaseUseCase<WsoData, UseCaseGetTokenWso2.InputParams>() {

    override suspend fun FlowCollector<ResultState<WsoData>>.run(params: InputParams) {
        emit(repository.getToken(params.token, params.params))
    }

    data class Params(
        val grant_type: String,
        val username: String,
        val password: String,
        val scope: String,
    )

    data class InputParams(
        val token: String,
        val params: Params
    ) : UseCaseParameters
}
