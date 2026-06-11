package com.mobile.base.core.core.domain.usecases.chatpay

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AiPayResponse
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseAiPay(private val repositoryChatPay: RepositoryChatPay) :
    BaseUseCase<AiPayResponse.AiPayResult, UseCaseAiPay.Params>() {

    override suspend fun FlowCollector<ResultState<AiPayResponse.AiPayResult>>.run(params: Params) {
        emit(repositoryChatPay.submitAiPay(params))
    }

    data class Params(
        @SerializedName("request_type") val requestType: String = "",
        @SerializedName("request_content") val requestContent: String = "",
        @SerializedName("request_user") val requestUser: String = ""
    ) : UseCaseParameters

    companion object {
        const val REQUEST_TYPE_TEXT = "TEXT"
        const val REQUEST_TYPE_IMAGE = "IMAGE"
    }
}
