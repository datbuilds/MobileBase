package com.mobile.base.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.data.entities.beneficiary.Bank

class BankResponse : BaseResponse<BankResponse.BankData>() {
    data class BankData(
        @SerializedName("array") val array: List<Bank>? = null
    )
}
