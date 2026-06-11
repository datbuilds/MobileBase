package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.TransactionTransferConfirm
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseTransactionTransferConfirm(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionTransferConfirm, UseCaseTransactionTransferConfirm.Params>() {
    override suspend fun FlowCollector<ResultState<TransactionTransferConfirm>>.run(
        params: Params
    ) {
        emit(repoTransfer.confirmTransaction(params))
    }

    data class Params(
        val transactionId: String,
        val confirmStatus: String,
        val otp: String
    ) : UseCaseParameters

    data class BodyParams(
        val confirmStatus: String,
        val otp: String,
    ) : UseCaseParameters
}