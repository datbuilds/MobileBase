package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TransactionDetail(
    @SerializedName("refNo") val refNo: String = "",
    @SerializedName("ordAccount") val ordAccount: String = "",
    @SerializedName("ordAccType") val ordAccType: String = "",
    @SerializedName("benAccount") val benAccount: String = "",
    @SerializedName("benAccType") val benAccType: String = "",
    @SerializedName("transDate") val transDate: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("remarks") val remarks: String = "",
    @SerializedName("currency") val currency: String = "",
    @SerializedName("accountName") val accountName: String = ""
) : Serializable
