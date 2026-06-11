package com.mobile.base.core.core.domain.usecases.transfer

import com.google.gson.annotations.SerializedName
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

data class FundTransferRequest(
    @SerializedName("orderDetail") val orderDetail: OrderDetail,
    @SerializedName("sender") val sender: AccountInfoRequest,
    @SerializedName("beneficiary") val beneficiary: AccountInfoRequest
) : UseCaseParameters

data class OrderDetail(
    @SerializedName("paymentType") val paymentType: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("currency") val currency: String = "",
    @SerializedName("remark") val remark: String = "",
    @SerializedName("transactionId") val transactionId: Int? = null,
    @SerializedName("saveNewAccount") val saveNewAccount: Boolean = false
)

data class AccountInfoRequest(
    @SerializedName("accountNo") val accountNo: String = ""
)

