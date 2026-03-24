package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AiPayResponse
import vn.shb.core.core.domain.source.service.ServiceAiPay
import vn.shb.core.core.domain.usecases.chatpay.RepositoryChatPay
import vn.shb.core.core.domain.usecases.chatpay.UseCaseAiPay

class RepositoryAiPayImpl(
    private val serviceAiPay: ServiceAiPay,
) : RepositoryChatPay, RepositoryBaseImpl() {

    override suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultSHB<AiPayResponse.AiPayResult> {
        return when (val result = serviceAiPay.submitAiPay(params)) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultSHB.Success(contentResult.data ?: AiPayResponse.AiPayResult())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultSHB.Failure -> handleFailure(result.reason)
            ResultSHB.Loading -> ResultSHB.Loading
        }
    }
}
