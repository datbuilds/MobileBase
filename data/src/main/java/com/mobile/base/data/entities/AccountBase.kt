package com.mobile.base.data.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

abstract class AccountBase(
    @SerializedName("accountNumber") var accountNumber: String = "",
    @SerializedName("accountType") val accountType: String = "",
    @SerializedName("currencyCode") var currencyCode: String = "",
    @SerializedName("positionDescription") val positionDescription: String = "",
    @SerializedName("productDescription") var productDescription: String = "",
    @SerializedName("availableBalance") val availableBalance: Double = 0.0,
    var customerName : String = ""
) :
    Serializable {
    abstract fun isSelected(): Boolean
    abstract fun setSelected(selected: Boolean)
    abstract fun getAvailableBalance(): String

}