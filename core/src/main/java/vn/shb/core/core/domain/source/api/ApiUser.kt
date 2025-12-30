package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vn.shb.core.core.domain.source.response.AccountDetailsResponse
import vn.shb.core.core.domain.source.response.AccountsInfoResponse
import vn.shb.core.core.domain.source.response.TransactionResponse
import vn.shb.core.core.domain.source.response.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.PUT
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.domain.source.request.ChangePasswordRequest
import vn.shb.core.core.domain.source.request.DefaultAccountRequest
import vn.shb.core.core.domain.source.response.DefaultAccountResponse
import vn.shb.core.core.domain.source.response.BeneficiaryResponse

interface ApiUser {
    @GET(ENDPOINT.BENEFICIARIES)
    fun getBeneficiaries(): Call<BeneficiaryResponse>

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

    @PUT(ENDPOINT.ACCOUNTS_DEFAULT)
    fun setDefaultAccount(@Body request: DefaultAccountRequest): Call<DefaultAccountResponse>

    @PUT(ENDPOINT.PASSWORDS)
    fun changePassword(@Body request: ChangePasswordRequest): Call<vn.shb.core.core.domain.source.response.ChangePasswordResponse>
}