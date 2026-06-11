package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AiPayResponse
import com.mobile.base.core.core.domain.source.service.ServiceAiPay
import com.mobile.base.core.core.domain.usecases.chatpay.RepositoryChatPay
import com.mobile.base.core.core.domain.usecases.chatpay.UseCaseAiPay

class RepositoryAiPayImpl(
    private val serviceAiPay: ServiceAiPay,
) : RepositoryChatPay, RepositoryBaseImpl() {

    override suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultState<AiPayResponse.AiPayResult> {
        return when (val result = serviceAiPay.submitAiPay(params)) {
            is ResultState.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultState.Success(contentResult.data ?: AiPayResponse.AiPayResult())
                } else {
                    handleFailure(contentResult)
                }
            }

            is ResultState.Failure -> handleFailure(result.reason)
            ResultState.Loading -> ResultState.Loading
        }
    }
}
