package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vn.shb.core.core.domain.source.response.AccountDetailsResponse
import vn.shb.core.core.domain.source.response.AccountsInfoResponse
import vn.shb.core.core.domain.source.response.TransactionResponse
import vn.shb.core.core.domain.source.response.UserInfoResponse

interface ApiUser {
    @GET(ENDPOINT.USER_INFO)
    fun getUserInfo(): Call<UserInfoResponse>

    @GET(ENDPOINT.ACCOUNTS_INFO)
    fun getAccountsInfo(): Call<AccountsInfoResponse>

    @GET(ENDPOINT.ACCOUNT_DETAILS)
    fun getAccountDetails(@Query("accountNumber") accountNumber: String): Call<AccountDetailsResponse>

    @GET(ENDPOINT.TRANSACTION)
    fun getTransactions(
        @Query("accountNumber") accountNumber: String? = null,
        @Query("queryType") queryType: String? = null,
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): Call<TransactionResponse>
}