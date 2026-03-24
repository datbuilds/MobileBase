package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import java.io.Serializable

class AiPayResponse : BaseResponse<AiPayResponse.AiPayResult>() {
    data class AiPayResult(
        @SerializedName("accountNum") val accountNum: String? = null,
        @SerializedName("amount") val amount: Int? = null,
        @SerializedName("beneficiaryBank") val beneficiaryBank: String? = null,
        @SerializedName("beneficiaryName") val beneficiaryName: String? = null,
        @SerializedName("shortName") val shortName: String? = null,
        @SerializedName("currency") val currency: String? = null,
        @SerializedName("remark") val remark: String? = null,
        @SerializedName("responseType") val responseType: String? = null
    ) : Serializable
}

typealias AiPayResult = AiPayResponse.AiPayResult
