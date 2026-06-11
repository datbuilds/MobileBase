package com.mobile.base.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import com.mobile.base.core.core.delivery.BaseResponse

class ValidateAccountResponse : BaseResponse<ValidateAccountResponse.ValidateAccountData>() {
    data class ValidateAccountData(
        @SerializedName("accountNumber") val accountNumber: String? = null,
        @SerializedName("bankCode") val bankCode: String? = null,
        @SerializedName("accountName") val accountName: String? = null
    )
}
