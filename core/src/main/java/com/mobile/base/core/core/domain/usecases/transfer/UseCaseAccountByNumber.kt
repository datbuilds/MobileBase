package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AccountUserNameModel
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseAccountByNumber(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<AccountUserNameModel, UseCaseAccountByNumber.Params>() {
    override suspend fun FlowCollector<ResultState<AccountUserNameModel>>.run(
        params: Params
    ) {
        emit(repoTransfer.getAccountByNumber(accountNumber = params.accountNumber))
    }

    data class Params(
        val accountNumber: String
    ) : UseCaseParameters
}