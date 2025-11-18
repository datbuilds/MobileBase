package vn.shb.data.entities.home.beneficiary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BeneficiaryUser(
    @SerializedName("refNo") val nameUser: String = "",
    @SerializedName("ordAccount") val accountNumber: String = "",
    @SerializedName("ordAccType") val nameAddressBank: String = "",
    @SerializedName("benAccount") val bankCode: String = "",
) : Serializable
