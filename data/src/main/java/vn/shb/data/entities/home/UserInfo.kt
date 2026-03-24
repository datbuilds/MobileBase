package vn.shb.data.entities.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("customerId") val customerId: String = "",
    @SerializedName("channelId") val channelId: String = "",
    @SerializedName("username") val username: String = "",
    @SerializedName("authMethodName") val authMethodName: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("customerName") val customerName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("pkgLimitId") val pkgLimitId: Int = 0,
    @SerializedName("isOverrideLimit") val isOverrideLimit: Boolean = false,
    @SerializedName("limitAmountIntra") val limitAmountIntra: Double = 0.0,
    @SerializedName("limitAmountInter") val limitAmountInter: Double = 0.0,
    @SerializedName("limitAmountStock") val limitAmountStock: Double = 0.0,
    @SerializedName("currentAmountIntra") val currentAmountIntra: Double = 0.0,
    @SerializedName("currentAmountStock") val currentAmountStock: Double = 0.0,
    @SerializedName("currentAmountInter") val currentAmountInter: Double = 0.0,
    @SerializedName("isEnabled") val isEnabled: Boolean = false,
    @SerializedName("isActivated") val isActivated: Boolean = false,
    @SerializedName("reqPwdChange") val reqPwdChange: Boolean = false,
    @SerializedName("authMethod") val authMethod: Int = 0,
    @SerializedName("lastAuthMethod") val lastAuthMethod: Int = 0,
    @SerializedName("authInfoExt1") val authInfoExt1: String = "",
    @SerializedName("lastAuthInfoExt1") val lastAuthInfoExt1: String = "",
    @SerializedName("defaultAcct") val defaultAcct: String? = null,
    @SerializedName("defaultLang") val defaultLang: String? = null,
    @SerializedName("openDate") val openDate: String = "",
    @SerializedName("openBranch") val openBranch: String = "",
    @SerializedName("regDate") val regDate: String = "",
    @SerializedName("regBranch") val regBranch: String = ""
) : Serializable {}