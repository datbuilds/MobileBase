package com.mobile.base.data.entities.login

import com.google.gson.annotations.SerializedName

data class RegisterDeviceData(
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("maskedPhoneNumber") val maskedPhoneNumber: String? = null,
    @SerializedName("expiresInSeconds") val expiresInSeconds: Int? = null,
    @SerializedName("remainingSeconds") val remainingSeconds: Int? = null,
    @SerializedName("otpCode") val otpCode: String? = null,
    @SerializedName("maxAttempts") val maxAttempts: Int? = null,
    @SerializedName("maxOtpRequestsPerWindow") val maxOtpRequestsPerWindow: Int? = null,
    var errorCode: String? = null
)
