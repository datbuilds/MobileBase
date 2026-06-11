package com.mobile.base.core.core.domain.source.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.mobile.base.core.core.domain.source.response.AccountUserNameResponse
import com.mobile.base.core.core.domain.source.response.ExchangeRateResponse
import com.mobile.base.core.core.domain.source.response.TransactionDetailResponse
import com.mobile.base.core.core.domain.source.response.TransactionTransferConfirmResponse
import com.mobile.base.core.core.domain.source.response.TransactionTransferResponse
import com.mobile.base.core.core.domain.source.response.TransferAccountResponse
import com.mobile.base.core.core.domain.source.response.ValidateTransactionResponse
import com.mobile.base.core.core.domain.usecases.transfer.FundTransferRequest
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseValidateTransaction

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
        @Query("mdCode") mdCode: String = "",
        @Query("transCode") transCode: String = "",
        @Query("transDate") transDate: String = "",
    ): Response<TransactionDetailResponse>

    @POST(ENDPOINT.VALIDATE_TRANSFER)
    suspend fun validateTransfer(@Body params: UseCaseValidateTransaction.Params)
            : Response<ValidateTransactionResponse>

    @GET(ENDPOINT.EXCHANGE_RATE)
    suspend fun getExchangeRates(
        @Query("sourceCurrency") sourceCurrency: String,
        @Query("targetCurrency") targetCurrency: String
    ): Response<ExchangeRateResponse>
}