package com.mobile.base.core.core.domain.usecases.transfer

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseValidateTransaction(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<String, UseCaseValidateTransaction.Params>() {
    override suspend fun FlowCollector<ResultState<String>>.run(
        params: Params
    ) {
        emit(repoTransfer.validateTransaction(params))
    }

    data class Params(
        @SerializedName("orderDetail") val orderDetail: OrderTransaction,
        @SerializedName("sender") val sender: AccountTransaction,
        @SerializedName("beneficiary") val beneficiary: AccountTransaction
    ) : UseCaseParameters

    data class OrderTransaction(
        @SerializedName("paymentType") val paymentType: String = "",
        @SerializedName("amount") val amount: Double = 0.0,
        @SerializedName("currency") val currency: String
    )

    data class AccountTransaction(
        @SerializedName("accountNo") val accountNo: String = ""
    )
}