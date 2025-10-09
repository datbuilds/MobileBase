package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountInfo(
    @SerializedName("casaTotal") val casaTotal: Double = 0.0,
    @SerializedName("tideTotal") val tideTotal: Double = 0.0,
    @SerializedName("loanTotal") val loanTotal: Double = 0.0,
    @SerializedName("accountType") val accountType: String = "",
    @SerializedName("accountTypeName") val accountTypeName: String = "",
    @SerializedName("accountNumber") val accountNumber: String = "",
    @SerializedName("currencyCode") val currencyCode: String = "",
    @SerializedName("positionCode") val positionCode: Int = 0,
    @SerializedName("positionDescription") val positionDescription: String = "",
    @SerializedName("productCode") val productCode: String = "",
    @SerializedName("productDescription") val productDescription: String = "",
    @SerializedName("availableBalance") val availableBalance: Double = 0.0,
    var isSelected: Boolean = false
) : Serializable
