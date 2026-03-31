package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.transfer.TransferAccount
import java.io.Serializable
import java.math.BigDecimal

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
    @SerializedName("otp") val otp: String = "",
    @SerializedName("authMethod") val authMethod: String = "",
    @SerializedName("authSms") val authSms: String? = null,
    @SerializedName("paymentType") val paymentType: String = "",
    @SerializedName("expireInSeconds") val expireInSeconds: Int = 0,
    @SerializedName("fee") val fee: Int = 0,
    @SerializedName("otpRemainingSeconds") val otpRemainingSeconds: Int = 0,
    @SerializedName("otpExpirySeconds") val otpExpirySeconds: Int = 0,
    @SerializedName("maxOtpRequestsPerWindow") val maxOtpRequestsPerWindow: Int = 0,
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("remainingSeconds") val remainingSeconds: Int = 0,
    @SerializedName("maxAttempts") val maxAttempts: Int = 0,
) : Serializable

data class TransactionTransferConfirm(
    @SerializedName("transactionId") val transactionId: String = "",
    @SerializedName("refNo") val refNo: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("mdCode") val moduleCode: String = "",
    @SerializedName("transCode") val transactionCode: String = "",
    @SerializedName("transDate") val transactionDate: String = "",
    @SerializedName("isValid") val isValid: Boolean = false,
    @SerializedName("remainingAttempts") val remainingAttempts: String = "",
    @SerializedName("remainingSeconds") val remainingSeconds: Int? = null,
    @SerializedName("maxAttempts") val maxAttempts: Int? = null,
) : Serializable

data class AccountUserNameModel(
    @SerializedName("customerName") val customerName : String = "",
    @SerializedName("currency") val currency : String = "",
    @SerializedName("productCode") val productCode : String = "",
    @SerializedName("productDescription") val productDescription : String = "",
    var accountNumber : String = "",
)

class ExchangeRateResponse : BaseResponse<ExchangeRateModel>()

data class ExchangeRateModel(
    @SerializedName("sourceCurrency") val sourceCurrency: String = "",
    @SerializedName("targetCurrency") val targetCurrency: String = "",
    @SerializedName("exchangeRate") val exchangeRate: BigDecimal? = BigDecimal.ZERO
) : Serializable
