package com.mobile.base.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AccountDetailsData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters


class UseCaseAccountDetails(private val repository: RepositoryUser) :
    BaseUseCase<AccountDetailsData, UseCaseAccountDetails.Params>() {
    override suspend fun FlowCollector<ResultState<AccountDetailsData>>.run(
        params: Params
    ) {
        emit(repository.getAccountDetails(params))
    }

    data class Params(val accountNumber: String) : UseCaseParameters
}