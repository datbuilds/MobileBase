package vn.shb.core.core.domain.source.request

import com.google.gson.annotations.SerializedName

data class DefaultAccountRequest(
    @SerializedName("accountNo")
    val accountNo: String
)
