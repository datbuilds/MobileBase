package vn.shb.data.entities.beneficiary

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Beneficiary(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("transactionType") val transactionType: String? = null,
    @SerializedName("accountNumber") val accountNumber: String? = null,
    @SerializedName("accountName") val accountName: String? = null,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("bankCode") val bankCode: String? = null,
    @SerializedName("lastChange") val lastChange: String? = null,
    @SerializedName("bankName") val bankName: String? = null
) : Parcelable
