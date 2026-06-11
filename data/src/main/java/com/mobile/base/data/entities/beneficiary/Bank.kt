package com.mobile.base.data.entities.beneficiary

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Bank(
    @SerializedName("code") val bankCode: String? = null,
    @SerializedName("fullName") val bankName: String? = null,
    @SerializedName("shortName") val shortName: String? = null,
    @SerializedName("logo") val logo: String? = null
) : Parcelable
