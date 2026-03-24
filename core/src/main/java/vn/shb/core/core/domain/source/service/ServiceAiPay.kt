package vn.shb.core.core.domain.source.service

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.api.ApiAiPay
import vn.shb.core.core.domain.source.response.AiPayResponse
import vn.shb.core.core.domain.usecases.chatpay.UseCaseAiPay
import vn.shb.core.core.retrofit.SafeExecute

class ServiceAiPay(private val api: ApiAiPay) : SafeExecute() {

    suspend fun submitAiPay(params: UseCaseAiPay.Params): ResultSHB<AiPayResponse> = execute {
        api.submitAiPay(params)
    }
}
