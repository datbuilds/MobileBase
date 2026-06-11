package com.mobile.base.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AccountData
import com.mobile.base.core.core.domain.source.response.AccountDetailsData
import com.mobile.base.core.core.domain.source.response.TransactionData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseTransaction(private val repository: RepositoryUser) : BaseUseCase<TransactionData, UseCaseTransaction.Params>() {

    override suspend fun FlowCollector<ResultState<TransactionData>>.run(
        params: Params
    ) {
        emit(repository.getTransactions(params))
    }

    data class Params(
        val accountNumber: String? = null,
        val queryType : String? = null,
        val fromDate: String? = null,
        val toDate: String? = null,
    ) : UseCaseParameters
}