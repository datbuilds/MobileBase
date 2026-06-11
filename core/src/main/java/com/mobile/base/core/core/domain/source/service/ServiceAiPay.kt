package com.mobile.base.core.core.domain.source.service

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.api.ApiAiPay
import com.mobile.base.core.core.domain.source.response.AiPayResponse
import com.mobile.base.core.core.domain.usecases.chatpay.UseCaseAiPay
import com.mobile.base.core.core.retrofit.SafeExecute

class ServiceAiPay(private val api: ApiAiPay) : SafeExecute() {

    suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultState<AiPayResponse> = execute {
        api.submitAiPay(params)
    }
}
