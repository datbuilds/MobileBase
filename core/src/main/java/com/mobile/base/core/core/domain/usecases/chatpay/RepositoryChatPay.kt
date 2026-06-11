package com.mobile.base.core.core.domain.usecases.chatpay

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AiPayResponse

interface RepositoryChatPay {
    suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultState<AiPayResponse.AiPayResult>
}
