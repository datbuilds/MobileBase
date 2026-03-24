package vn.shb.data.entities.login

import com.google.gson.annotations.SerializedName

data class VerifyDeviceRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("transactionId") val transactionId: String,
    @SerializedName("otpCode") val otpCode: String
)
