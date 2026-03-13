package vn.shb.core.core.domain.usecases.transfer

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseValidateTransaction(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<String, UseCaseValidateTransaction.Params>() {
    override suspend fun FlowCollector<ResultSHB<String>>.run(
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
        @SerializedName("amount") val amount: Double = 0.0
    )

    data class AccountTransaction(
        @SerializedName("accountNo") val accountNo: String = ""
    )
}