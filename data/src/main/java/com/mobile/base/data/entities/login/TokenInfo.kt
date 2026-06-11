package com.mobile.base.data.entities.login

import java.io.Serializable

data class TokenInfo(
    val brand: String = "",
    val imei: String = "",
    val jailbreak: Int = 0,
    val lastActivatedDate: String = "",
    val lastActivatedTime: String = "",
    val message: String = "",
    val model: String = "",
    val phoneNumber: String = "",
    val responseCode: Int = 0,
    val userID: String = "",
    val userName: String = ""
) : Serializable {
//    fun isSuccess() = message.contentEquals("Success")

    fun isHaveImeiAndSameDevice(androidId: String) =
        imei.isNotEmpty() && imei.contentEquals(androidId)

    fun isHaveImeiAndNotSameDevice(androidId: String) =
        imei.isNotEmpty() && !imei.contentEquals(androidId)

    fun isNotInformation(userLogin: String) = userName.contentEquals(userLogin)

    fun isDeviceRoot() = jailbreak != 0
}