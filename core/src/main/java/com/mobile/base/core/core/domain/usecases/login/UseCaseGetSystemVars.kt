package com.mobile.base.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.SystemVarData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseGetSystemVars(private val repository: RepositoryAuth) :
    BaseUseCase<SystemVarData, UseCaseGetSystemVars.Params>() {

    override suspend fun FlowCollector<ResultState<SystemVarData>>.run(params: Params) {
        emit(repository.getSystemVars(params.name))
    }

    data class Params(
        val name: String = ""
    ) : UseCaseParameters
}
