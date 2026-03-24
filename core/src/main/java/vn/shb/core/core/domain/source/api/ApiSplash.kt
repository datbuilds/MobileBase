package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import vn.shb.core.core.domain.source.response.VersionResponse

interface ApiSplash {

    @GET(ENDPOINT.APP_VERSION)
    fun checkVersion(
        @Query("platform") platform: String = "android",
        @Query("appType") appType: String = "user",
        @Query("version") version: String
    ): Call<VersionResponse>
}