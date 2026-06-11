package com.mobile.base.core.core.domain.usecases.transfer

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AccountUserNameModel
import com.mobile.base.core.core.domain.source.response.ExchangeRateModel
import com.mobile.base.core.core.domain.source.response.TransactionTransfer
import com.mobile.base.core.core.domain.source.response.TransactionTransferConfirm
import com.mobile.base.core.core.domain.source.response.TransferAccountData
import com.mobile.base.data.entities.home.TransactionDetail

interface RepositoryTransfer {
    suspend fun getTransferAccount(): ResultState<TransferAccountData>

    suspend fun getReceiverAccount(): ResultState<TransferAccountData>

    suspend fun postTransaction(request : FundTransferRequest): ResultState<TransactionTransfer>

    suspend fun confirmTransaction(request : UseCaseTransactionTransferConfirm.Params): ResultState<TransactionTransferConfirm>

    suspend fun getAccountByNumber(accountNumber : String) : ResultState<AccountUserNameModel>

    suspend fun getTransactionDetail(params: UseCaseTransactionDetail.Params): ResultState<TransactionDetail>

    suspend fun validateTransaction(params : UseCaseValidateTransaction.Params) : ResultState<String>

    suspend fun getExchangeRates(sourceCurrency: String, targetCurrency: String): ResultState<ExchangeRateModel>
}