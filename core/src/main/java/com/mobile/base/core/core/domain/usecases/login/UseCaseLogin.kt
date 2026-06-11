package com.mobile.base.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.login.UserLog

class UseCaseLogin(private val repository: RepositoryAuth) :
    BaseUseCase<UserLog, UseCaseLogin.Params>() {

    override suspend fun FlowCollector<ResultState<UserLog>>.run(params: Params) {
        emit(repository.login(params))
    }

    data class  Params(
        val username: String = "",
        val password: String = ""
    ) : UseCaseParameters
}
