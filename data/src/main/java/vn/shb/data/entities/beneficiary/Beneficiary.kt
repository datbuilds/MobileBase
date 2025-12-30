package vn.shb.data.entities.beneficiary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Beneficiary(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("transactionType") val transactionType: String? = null,
    @SerializedName("accountNumber") val accountNumber: String? = null,
    @SerializedName("accountName") val accountName: String? = null,
    @SerializedName("accountNick") val accountNick: String? = null,
    @SerializedName("defaultTransactionDescription") val defaultTransactionDescription: String? = null,
    @SerializedName("bankCode") val bankCode: String? = null,
    @SerializedName("bankName") val bankName: String? = null,
    @SerializedName("bankBranch") val bankBranch: String? = null,
    @SerializedName("bankCity") val bankCity: String? = null,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("serviceId") val serviceId: String? = null,
    @SerializedName("lastChange") val lastChange: String? = null,
    @SerializedName("payCodeLbl") val payCodeLbl: String? = null,
    @SerializedName("payCodeLblEn") val payCodeLblEn: String? = null
) : Serializable
