package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.getBalance

data class AccountInfo(
    @SerializedName("casaTotal") val casaTotal: Double = 0.00,
    @SerializedName("tideTotal") val tideTotal: Double = 0.00,
    @SerializedName("loanTotal") val loanTotal: Double = 0.00,
    @SerializedName("accountTypeName") val accountTypeName: String = "",
    @SerializedName("positionCode") val positionCode: Int = 0,
    @SerializedName("productCode") val productCode: String = "",
    var select: Boolean = false
) : AccountBase() {
    override fun getAvailableBalance(): String = availableBalance.getBalance()
    override fun isSelected() = select
    override fun setSelected(selected: Boolean) {
        select = selected
    }
}
