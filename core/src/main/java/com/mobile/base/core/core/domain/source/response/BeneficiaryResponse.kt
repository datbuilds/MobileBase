package com.mobile.base.core.core.domain.source.response

import androidx.annotation.Keep
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.data.entities.beneficiary.Beneficiary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BeneficiaryResponse : BaseResponse<BeneficiaryResponse.BeneficiaryData>() {
    data class BeneficiaryData(
        @SerializedName("array") val array: List<Beneficiary>? = null
    )
}
