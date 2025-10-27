package vn.shb.data.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

abstract class AccountBase(
    @SerializedName("accountNumber") val accountNumber: String = "",
    @SerializedName("accountType") val accountType: String = "",
    @SerializedName("currencyCode") val currencyCode: String = "",
    @SerializedName("positionDescription") val positionDescription: String = "",
    @SerializedName("productDescription") val productDescription: String = "",
    @SerializedName("availableBalance") val availableBalance: Double = 0.0,
) :
    Serializable {
    abstract fun isSelected(): Boolean
    abstract fun setSelected(selected: Boolean)
    abstract fun getAvailableBalance(): String

}