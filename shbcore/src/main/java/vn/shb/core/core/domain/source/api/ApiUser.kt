package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.GET
import vn.shb.core.core.domain.source.response.UserInfoResponse

interface ApiUser {
    @GET(ENDPOINT.USER_INFO)
    fun getUserInfo(): Call<UserInfoResponse>
}