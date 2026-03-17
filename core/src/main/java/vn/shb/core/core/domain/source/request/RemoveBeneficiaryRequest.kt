package vn.shb.core.core.domain.source.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RemoveBeneficiaryRequest(
    @SerializedName("accountNumber")
    val accountNumber: String?,
    @SerializedName("bankCode")
    val bankCode: String?
)