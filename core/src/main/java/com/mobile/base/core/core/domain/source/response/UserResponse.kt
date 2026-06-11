package com.mobile.base.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import com.mobile.base.data.entities.home.AccountDetails
import com.mobile.base.data.entities.home.AccountInfo
import com.mobile.base.data.entities.home.TransactionDetail
import com.mobile.base.data.entities.home.TransactionItem
import com.mobile.base.data.entities.home.UserInfo
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

