package vn.shb.data.entities.beneficiary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Bank(
    @SerializedName("code") val bankCode: String? = null,
    @SerializedName("fullName") val bankName: String? = null,
    @SerializedName("shortName") val shortName: String? = null,
    @SerializedName("logo") val logo: String? = null
) : Serializable
