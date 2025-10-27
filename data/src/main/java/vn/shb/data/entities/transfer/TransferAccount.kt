package vn.shb.data.entities.transfer

import com.google.gson.annotations.SerializedName
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.getBalance


data class TransferAccount(
    @SerializedName("finSt") val finSt: String = "",
    @SerializedName("finStEn") val finStEn: String = "",
    @SerializedName("inactiveStatus") val inactiveStatus: String = "",
    @SerializedName("positionCode") val positionCode: Int = 0,
    @SerializedName("productCode") val productCode: String = "",
    @SerializedName("currentBalance") val currentBalance: Double = 0.00,
    @SerializedName("rate") val rate: Double = 0.00,
    @SerializedName("isCoHolder") val isCoHolder: Boolean = false,
    var select: Boolean = false
) : AccountBase() {
    override fun getAvailableBalance(): String = availableBalance.getBalance()

    override fun setSelected(selected: Boolean) {
        select = selected
    }
    override fun isSelected(): Boolean {
        return select
    }
}