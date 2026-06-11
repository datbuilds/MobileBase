package com.mobile.base.data.entities.wso2

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WsoData(
    @SerializedName("access_token") val access_token: String = "",
    @SerializedName("refresh_token") val refresh_token: String = "",
    @SerializedName("scope") val scope: String = "",
    @SerializedName("token_type") val token_type: String = "",
    @SerializedName("expires_in") val expires_in: Int = 0
) : Serializable {

    fun expireIn() = expires_in.toLong()

}
