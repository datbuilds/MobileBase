package vn.shb.data.entities.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserLog(
    @SerializedName("access_token") val access_token: String = "",
    @SerializedName("token_type") val token_type: String = "",
    @SerializedName("expired_in") val expires_in: Int = 0,
    @SerializedName("refresh_token") val refresh_token: String = "",
    @SerializedName("refresh_expired_in") val refresh_expires_in: Int = 0,
    @SerializedName("session_state") val session_state: String = "",
    @SerializedName("id_token") val id_token: String = "",
    @SerializedName("username") var username: String = "",
    @SerializedName("userLog") val userLogin: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("scope") val scope: String = "",
    @SerializedName("loginFailCount") val loginFailCount: String = "",
    @SerializedName("lockedUntil") val lockedUntil: String = "",

    var customerId : String = "",
) : Serializable {

    fun expireIn() = expires_in.toLong()

    fun toUserString() = UserConverters.userInfoToString(this)
}
