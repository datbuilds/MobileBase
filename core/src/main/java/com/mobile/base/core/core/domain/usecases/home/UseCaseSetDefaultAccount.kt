package com.mobile.base.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseSetDefaultAccount(private val repository: RepositoryUser) :
    BaseUseCase<Boolean, UseCaseSetDefaultAccount.Params>() {

    override suspend fun FlowCollector<ResultState<Boolean>>.run(params: Params) {
        emit(repository.setDefaultAccount(params.accountNo))
    }

    data class Params(val accountNo: String) : UseCaseParameters
}
