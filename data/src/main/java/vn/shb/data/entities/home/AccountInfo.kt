package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountInfo(
    @SerializedName("casaTotal") val casaTotal: Long = 0,
    @SerializedName("tideTotal") val tideTotal: Long = 0,
    @SerializedName("loanTotal") val loanTotal: Long = 0,
    @SerializedName("accountType") val accountType: String = "",
    @SerializedName("accountTypeName") val accountTypeName: String = "",
    @SerializedName("accountNumber") val accountNumber: String = "",
    @SerializedName("currencyCode") val currencyCode: String = "",
    @SerializedName("positionCode") val positionCode: Int = 0,
    @SerializedName("positionDescription") val positionDescription: String = "",
    @SerializedName("productCode") val productCode: String = "",
    @SerializedName("productDescription") val productDescription: String = "",
    @SerializedName("availableBalance") val availableBalance: Long = 0,
    var isSelected: Boolean = false
) : Serializable
