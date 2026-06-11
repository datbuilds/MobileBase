package com.mobile.base.data.entities.home

import com.google.gson.annotations.SerializedName
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.data.entities.getBalance

data class AccountDetails(
    @SerializedName("finSt") val finSt: String = "",
    @SerializedName("finStEn") val finStEn: String = "",
    @SerializedName("inactiveStatus") val inactiveStatus: String = "",
    @SerializedName("positionCode") val positionCode: Long = 0,
    @SerializedName("productCode") val productCode: String = "",
    @SerializedName("currentBalance") val currentBalance: Double = 0.0,
    @SerializedName("rate") val rate: Double = 0.0,
    @SerializedName("isCoHolder") val isCoHolder: Boolean = false
) : AccountBase() {
    override fun getAvailableBalance(): String = availableBalance.getBalance()

    override fun setSelected(selected: Boolean) {
    }
    override fun isSelected(): Boolean {
        return false
    }
}
