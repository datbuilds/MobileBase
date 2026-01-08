package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.transfer.TransferAccount
import java.io.Serializable
class TransferAccountResponse : BaseResponse<TransferAccountData>()

data class TransferAccountData(
    @SerializedName("array") val array: List<TransferAccount> = emptyList()
) : Serializable

class TransactionTransferResponse : BaseResponse<TransactionTransfer>()
class TransactionTransferConfirmResponse : BaseResponse<TransactionTransferConfirm>()

class AccountUserNameResponse : BaseResponse<AccountUserNameModel>()

data class TransactionTransfer(
    @SerializedName("transactionId") val transactionId: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("authMethod") val authMethod: String = "",
    @SerializedName("authSms") val authSms: String? = null,
    @SerializedName("paymentType") val paymentType: String = "",
    @SerializedName("expireInSeconds") val expireInSeconds: Int = 0
) : Serializable

data class TransactionTransferConfirm(
    @SerializedName("transactionId") val transactionId: Int = 0,
    @SerializedName("refNo") val refNo: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("mdCode") val moduleCode: String = "",
    @SerializedName("transCode") val transactionCode: String = "",
    @SerializedName("transDate") val transactionDate: String = ""
) : Serializable

data class AccountUserNameModel(
    @SerializedName("customerName") val customerName : String = "",
    @SerializedName("currency") val currency : String = "",
    @SerializedName("productCode") val productCode : String = "",
    @SerializedName("productDescription") val productDescription : String = "",
    var accountNumber : String = "",
)


