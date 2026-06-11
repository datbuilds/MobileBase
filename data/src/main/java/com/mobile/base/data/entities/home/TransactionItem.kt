package com.mobile.base.data.entities.home

import com.google.gson.annotations.SerializedName

sealed class TransactionItem {
    data class Header(val title: String) : TransactionItem()
    data class Transaction(
        @SerializedName("transactionDate") val transactionDate: String = "",
        @SerializedName("valueDate") val valueDate: String = "",
        @SerializedName("transactionTime") val transactionTime: String = "",
        @SerializedName("creditAmount") val creditAmount: Double = 0.0,
        @SerializedName("debitAmount") val debitAmount: Double = 0.0,
        @SerializedName("transactionDescription") val transactionDescription: String = "",
        @SerializedName("currencyCode") val currencyCode: String = "",
        @SerializedName("referenceNumber") val referenceNumber: String = "",
        @SerializedName("transactionDateFormatted") val transactionDateFormatted: String = "",
        @SerializedName("amountFormatted") val amountFormatted: String = "",
        @SerializedName("channelId") val channelId: String = "",
        @SerializedName("moduleCode") val moduleCode: String = "",
        @SerializedName("transactionCode") val transactionCode: String = ""
    ) : TransactionItem()
}