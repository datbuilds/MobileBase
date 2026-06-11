package com.mobile.base.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.home.TransactionDetail

class UseCaseTransactionDetail(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<TransactionDetail, UseCaseTransactionDetail.Params>() {
    override suspend fun FlowCollector<ResultState<TransactionDetail>>.run(
        params: Params
    ) {
        emit(repoTransfer.getTransactionDetail(params))
    }

    data class Params(
        val refNo: String,
        val acctNo: String,
        val drCrFlg: String,
        val mdCode: String,
        val transCode: String,
        val transDate: String,
    ) : UseCaseParameters
}