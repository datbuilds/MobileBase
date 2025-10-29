package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import vn.shb.core.core.domain.source.response.AccountUserNameResponse
import vn.shb.core.core.domain.source.response.TransactionDetailResponse
import vn.shb.core.core.domain.source.response.TransactionTransferConfirmResponse
import vn.shb.core.core.domain.source.response.TransactionTransferResponse
import vn.shb.core.core.domain.source.response.TransferAccountResponse
import vn.shb.core.core.domain.usecases.transfer.FundTransferRequest
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm

interface ApiTransfer {

    @GET(ENDPOINT.TRANSFER_ACCOUNT)
    fun getTransferAccount(@Query("currency") currency: String = "ALL"): Call<TransferAccountResponse>

    @GET(ENDPOINT.RECEIVE_ACCOUNT)
    fun getReceiverAccount(@Query("currency") currency: String = "ALL"): Call<TransferAccountResponse>

    @POST(ENDPOINT.TRANSFER_TRANSACTION)
    fun postTransaction(@Body request: FundTransferRequest): Call<TransactionTransferResponse>

    @PATCH(ENDPOINT.TRANSFER_TRANSACTION_CONFIRM)
    suspend fun confirmTransaction(
        @Path("transactionId") transactionId: String,
        @Body request: UseCaseTransactionTransferConfirm.BodyParams
    ): Response<TransactionTransferConfirmResponse>

    @GET(ENDPOINT.GET_ACCOUNT_BY_NUMBER)
    suspend fun getAccountByNumber(
        @Path("accountNumber") accountNumber: String,
    ): Response<AccountUserNameResponse>

    @GET(ENDPOINT.GET_TRANSACTION_DETAIL)
    suspend fun getTransactionDetail(
        @Query("refNo") refNo: String = "",
        @Query("acctNo") acctNo: String = "",
        @Query("drCrFlg") drCrFlg: String = "D",
    ): Response<TransactionDetailResponse>
}