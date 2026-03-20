package vn.shb.core.core.domain.usecases.chatpay

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AiPayResponse
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseAiPay(private val repositoryChatPay: RepositoryChatPay) :
    BaseUseCase<AiPayResponse.AiPayResult, UseCaseAiPay.Params>() {

    override suspend fun FlowCollector<ResultSHB<AiPayResponse.AiPayResult>>.run(params: Params) {
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
