package vn.shb.core.core.domain.source.response

import com.google.gson.annotations.SerializedName
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.UserInfo
import java.io.Serializable

class UserInfoResponse : BaseResponse<UserInfo>()

class AccountsInfoResponse : BaseResponse<AccountData>()

data class AccountData(
    @SerializedName("array") val array: List<AccountInfo> = emptyList()
) : Serializable