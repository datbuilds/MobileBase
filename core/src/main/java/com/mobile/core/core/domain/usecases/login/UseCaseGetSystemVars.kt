package com.mobile.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.core.core.delivery.ResultState
import com.mobile.core.core.domain.source.response.SystemVarData
import com.mobile.core.core.domain.usecases.BaseUseCase
import com.mobile.core.core.domain.usecases.UseCaseParameters

class UseCaseGetSystemVars(private val repository: RepositoryAuth) :
    BaseUseCase<SystemVarData, UseCaseGetSystemVars.Params>() {

    override suspend fun FlowCollector<ResultState<SystemVarData>>.run(params: Params) {
        emit(repository.getSystemVars(params.name))
    }

    data class Params(
        val name: String = ""
    ) : UseCaseParameters
}
