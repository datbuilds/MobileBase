package com.mobile.base.core.core.domain.source.request

import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.google.gson.annotations.SerializedName

data class ValidateAccountRequest(
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("bankCode") val bankCode: String
) : UseCaseParameters
