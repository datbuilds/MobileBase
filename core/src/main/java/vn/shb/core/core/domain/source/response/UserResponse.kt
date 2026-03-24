package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.TransactionDetail
import vn.shb.data.entities.home.TransactionItem
import vn.shb.data.entities.home.UserInfo
import java.io.Serializable

class UserInfoResponse : BaseResponse<UserInfo>()

class AccountsInfoResponse : BaseResponse<AccountData>()

class AccountDetailsResponse : BaseResponse<AccountDetailsData>()

class TransactionResponse : BaseResponse<TransactionData>()

class TransactionDetailResponse : BaseResponse<TransactionDetail>()

class ValidateTransactionResponse : BaseResponse<String>()

data class AccountData(
    @SerializedName("array") val array: List<AccountInfo> = emptyList()
) : Serializable

data class AccountDetailsData(
    @SerializedName("array") val array: List<AccountDetails> = emptyList()
) : Serializable

data class TransactionData(
    @SerializedName("array") val array: List<TransactionItem.Transaction>? = null
) : Serializable

