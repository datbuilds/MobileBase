package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseTransferAccount(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransferAccountData, UseCaseTransferAccount.Params>() {
    override suspend fun FlowCollector<ResultState<TransferAccountData>>.run(
        params: Params
    ) {
        if (params.isTransferAccount) {
            emit(repoTransfer.getTransferAccount())
        } else {
            emit(repoTransfer.getReceiverAccount())
        }
    }

    data class Params(
        val isTransferAccount: Boolean
    ) : UseCaseParameters
}