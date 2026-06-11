package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.ExchangeRateModel
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseExchangeRate(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<ExchangeRateModel, UseCaseExchangeRate.Params>() {

    override suspend fun FlowCollector<ResultState<ExchangeRateModel>>.run(params: Params) {
        emit(repoTransfer.getExchangeRates(params.sourceCurrency, params.targetCurrency))
    }

    data class Params(
        val sourceCurrency: String,
        val targetCurrency: String
    ) : UseCaseParameters
}
