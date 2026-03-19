package vn.shb.core.core.domain.usecases.chatpay

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AiPayResponse

interface RepositoryChatPay {
    suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultSHB<AiPayResponse.AiPayResult>
}
