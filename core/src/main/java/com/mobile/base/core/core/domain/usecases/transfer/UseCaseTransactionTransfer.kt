package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.TransactionTransfer
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UseCaseTransactionTransfer(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionTransfer, FundTransferRequest>() {
    override suspend fun FlowCollector<ResultState<TransactionTransfer>>.run(
        params: FundTransferRequest
    ) {
       emit(repoTransfer.postTransaction(params))
    }
}