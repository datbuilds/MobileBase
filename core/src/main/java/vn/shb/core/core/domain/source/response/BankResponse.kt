package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.beneficiary.Bank

class BankResponse : BaseResponse<BankResponse.BankData>() {
    data class BankData(
        @SerializedName("array") val array: List<Bank>? = null
    )
}
