package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountDetails(
    @SerializedName("accountNumber") val accountNumber: String = "",
    @SerializedName("finSt") val finSt: String = "",
    @SerializedName("finStEn") val finStEn: String = "",
    @SerializedName("inactiveStatus") val inactiveStatus: String = "",
    @SerializedName("currencyCode") val currencyCode: String = "",
    @SerializedName("positionCode") val positionCode: Long = 0,
    @SerializedName("positionDescription") val positionDescription: String = "",
    @SerializedName("productCode") val productCode: String = "",
    @SerializedName("productDescription") val productDescription: String = "",
    @SerializedName("currentBalance") val currentBalance: Double = 0.0,
    @SerializedName("availableBalance") val availableBalance: Double = 0.0,
    @SerializedName("rate") val rate: Double = 0.0,
    @SerializedName("isCoHolder") val isCoHolder: Boolean = false
) : Serializable
