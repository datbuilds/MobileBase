package com.mobile.base.core.core.domain.source.request

import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.google.gson.annotations.SerializedName

data class BeneficiaryRequest(
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("accountName") val accountName: String,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("accountNick") val accountNick: String? = null,
    @SerializedName("bankCode") var bankCode: String? = null
) : UseCaseParameters
