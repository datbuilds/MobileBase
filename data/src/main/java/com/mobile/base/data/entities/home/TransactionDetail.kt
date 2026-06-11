package com.mobile.base.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal

data class TransactionDetail(
    @SerializedName("refNo") val refNo: String = "",
    @SerializedName("ordAccount") val ordAccount: String? = "",
    @SerializedName("ordAccType") val ordAccType: String = "",
    @SerializedName("benAccount") val benAccount: String? = "",
    @SerializedName("benAccType") val benAccType: String = "",
    @SerializedName("transDate") val transDate: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("amountHis") val amountHis: Double = 0.0,
    @SerializedName("remarks") val remarks: String = "",
    @SerializedName("currency") val currency: String = "",
    @SerializedName("currencyHis") val currencyHis: String = "",
    @SerializedName("accountName") val accountName: String? = "",
    @SerializedName("ccyCdSrc") val ccyCdSrc: String = "",
    @SerializedName("ccyCdDst") val ccyCdDst: String = "",
    @SerializedName("hasBeneficiary") val hasBeneficiary: Boolean = false,
    @SerializedName("rate") val rate: BigDecimal = BigDecimal.valueOf(0.000001)
) : Serializable
