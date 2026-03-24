package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.ExchangeRateModel
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseExchangeRate(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<ExchangeRateModel, UseCaseExchangeRate.Params>() {

    override suspend fun FlowCollector<ResultSHB<ExchangeRateModel>>.run(params: Params) {
        emit(repoTransfer.getExchangeRates(params.sourceCurrency, params.targetCurrency))
    }

    data class Params(
        val sourceCurrency: String,
        val targetCurrency: String
    ) : UseCaseParameters
}
