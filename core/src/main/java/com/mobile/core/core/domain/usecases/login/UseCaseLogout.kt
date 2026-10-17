package com.mobile.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.core.core.delivery.ActionDone
import com.mobile.core.core.delivery.ResultState
import com.mobile.core.core.domain.usecases.BaseUseCase
import com.mobile.core.core.domain.usecases.None
import com.mobile.core.core.domain.usecases.UseCaseParameters

class UseCaseLogout(
    private val repository: RepositoryAuth
) : BaseUseCase<ActionDone, None>() {

    override suspend fun FlowCollector<ResultState<ActionDone>>.run(
        params: None
    ) {
        emit(repository.logout())
    }

    data class Params(
        val type: String = "ALL",
    ) : UseCaseParameters
}
