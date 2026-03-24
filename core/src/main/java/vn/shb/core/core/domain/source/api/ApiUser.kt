package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.Response
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
import retrofit2.http.HTTP
import retrofit2.http.PUT
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.delivery.EmptyResponse
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.request.ChangePasswordRequest
import vn.shb.core.core.domain.source.request.DefaultAccountRequest
import vn.shb.core.core.domain.source.request.RemoveBeneficiaryRequest
import vn.shb.core.core.domain.source.request.ValidateAccountRequest
import vn.shb.core.core.domain.source.response.DefaultAccountResponse
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.domain.source.response.ChangePasswordResponse
import vn.shb.core.core.domain.source.response.ValidateAccountResponse

interface ApiUser {
    @GET(ENDPOINT.BENEFICIARIES)
    suspend fun getBeneficiaries(): Response<BeneficiaryResponse>

    @retrofit2.http.POST(ENDPOINT.BENEFICIARIES + "/validate-account")
    suspend fun validateAccount(@Body request: ValidateAccountRequest): Response<ValidateAccountResponse>

    @retrofit2.http.POST(ENDPOINT.BENEFICIARIES)
    suspend fun createBeneficiary(@Body request: BeneficiaryRequest): Response<BeneficiaryResponse>

    @PUT(ENDPOINT.BENEFICIARIES )
    suspend fun updateBeneficiary( @Body request: BeneficiaryRequest): Response<BeneficiaryResponse>

    @HTTP(method = "DELETE", path = ENDPOINT.BENEFICIARIES, hasBody = true)
    suspend fun deleteBeneficiary(@Body request: RemoveBeneficiaryRequest): Response<EmptyResponse>

    @GET(ENDPOINT.BANKS)
    suspend fun getBanks(): Response<vn.shb.core.core.domain.source.response.BankResponse>

    @GET(ENDPOINT.USER_INFO)
    suspend fun getUserInfo(): Response<UserInfoResponse>

    @GET(ENDPOINT.ACCOUNTS_INFO)
    suspend fun getAccountsInfo(): Response<AccountsInfoResponse>

    @GET(ENDPOINT.ACCOUNT_DETAILS)
    suspend fun getAccountDetails(@Query("accountNumber") accountNumber: String): Response<AccountDetailsResponse>

    @GET(ENDPOINT.TRANSACTION)
    suspend fun getTransactions(
        @Query("accountNumber") accountNumber: String? = null,
        @Query("queryType") queryType: String? = null,
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): Response<TransactionResponse>

    @PUT(ENDPOINT.ACCOUNTS_DEFAULT)
    suspend fun setDefaultAccount(@Body request: DefaultAccountRequest): Response<DefaultAccountResponse>

    @PUT(ENDPOINT.PASSWORDS)
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
}