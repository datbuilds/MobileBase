package vn.shb.core.core.domain.source.response

import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.beneficiary.Beneficiary

import com.google.gson.annotations.SerializedName

class BeneficiaryResponse : BaseResponse<BeneficiaryResponse.BeneficiaryData>() {
    data class BeneficiaryData(
        @SerializedName("array") val array: List<Beneficiary>? = null
    )
}
