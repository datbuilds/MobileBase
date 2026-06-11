package com.mobile.base.data.entities.transfer

import com.google.gson.annotations.SerializedName
import com.mobile.base.data.entities.AccountBase
import java.io.Serializable


data class ConfirmationModel(
    @SerializedName("fromAccount") val fromAccount: AccountBase,
    @SerializedName("toAccount") val toAccount: AccountBase,
    @SerializedName("remarks") val remarks: String = "",
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("amount") val amount: Pair<Double,String>,
    @SerializedName("fee") val fee: Double = 0.0,
    @SerializedName("totalAmount") val totalAmount: Pair<Double,String>,
    @SerializedName("otp") val otp: String = "",
    @SerializedName("exchangeRateUSD") val exchangeRateScreen: String? = "",
    var paymentType: String
) : Serializable