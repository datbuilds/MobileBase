package vn.shb.data.entities.transfer

import com.google.gson.annotations.SerializedName
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.getBalance
import java.io.Serializable


data class ConfirmationModel(
    @SerializedName("fromAccount") val fromAccount: AccountBase,
    @SerializedName("toAccount") val toAccount: AccountBase,
    @SerializedName("remarks") val remarks: String = "",
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("fee") val fee: Double = 0.0,
    @SerializedName("totalAmount") val totalAmount: Double = 0.00,
    @SerializedName("otp") val otp: String = "",
    var select: Boolean = false
) : Serializable