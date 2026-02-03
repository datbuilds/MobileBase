package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import vn.shb.core.core.delivery.ActionDone
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

    @retrofit2.http.POST(ENDPOINT.BENEFICIARIES + "/validate-account")
    fun validateAccount(@Body request: vn.shb.core.core.domain.source.request.ValidateAccountRequest): Call<vn.shb.core.core.domain.source.response.ValidateAccountResponse>

    @retrofit2.http.POST(ENDPOINT.BENEFICIARIES)
    fun createBeneficiary(@Body request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): Call<vn.shb.core.core.domain.source.response.BeneficiaryResponse>

    @PUT(ENDPOINT.BENEFICIARIES + "/{id}")
    fun updateBeneficiary(@Path("id") id: String, @Body request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): Call<vn.shb.core.core.domain.source.response.BeneficiaryResponse>

    @DELETE(ENDPOINT.BENEFICIARIES + "/{id}")
    fun deleteBeneficiary(@Path("id") id: String): Call<vn.shb.core.core.delivery.EmptyResponse>


    @GET(ENDPOINT.BANKS)
    fun getBanks(): Call<vn.shb.core.core.domain.source.response.BankResponse>

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