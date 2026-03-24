package vn.shb.data.entities.login

import com.google.gson.annotations.SerializedName

data class RegisterDeviceRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)
