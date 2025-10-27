package vn.shb.core.core.domain.usecases.transfer

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.source.response.AccountDetailsData
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.source.response.TransactionData
import vn.shb.core.core.domain.source.response.TransactionTransfer
import vn.shb.core.core.domain.source.response.TransactionTransferConfirm
import vn.shb.core.core.domain.source.response.TransactionTransferResponse
import vn.shb.core.core.domain.source.response.TransferAccountData
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.data.entities.home.UserInfo

interface RepositoryTransfer {
    suspend fun getTransferAccount(): ResultSHB<TransferAccountData>

    suspend fun getReceiverAccount(): ResultSHB<TransferAccountData>

    suspend fun postTransaction(request : FundTransferRequest): ResultSHB<TransactionTransfer>

    suspend fun confirmTransaction(request : UseCaseTransactionTransferConfirm.Params): ResultSHB<TransactionTransferConfirm>

    suspend fun getAccountByNumber(accountNumber : String) : ResultSHB<AccountUserNameModel>
}