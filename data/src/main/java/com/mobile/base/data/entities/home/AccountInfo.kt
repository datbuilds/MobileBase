package com.mobile.base.data.entities.home

import com.google.gson.annotations.SerializedName
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.data.entities.getBalance

data class AccountInfo(
    @SerializedName("casaTotal") val casaTotal: Double = 0.00,
    @SerializedName("tideTotal") val tideTotal: Double = 0.00,
    @SerializedName("loanTotal") val loanTotal: Double = 0.00,
    @SerializedName("accountTypeName") val accountTypeName: String = "",
    @SerializedName("positionCode") val positionCode: Int = 0,
    @SerializedName("productCode") var productCode: String = "",
    var select: Boolean = false
) : AccountBase() {
    override fun getAvailableBalance(): String = availableBalance.getBalance()
    override fun isSelected() = select
    override fun setSelected(selected: Boolean) {
        select = selected
    }

    fun setValueAccountNumber(number : String){
        accountNumber = number
    }
}
