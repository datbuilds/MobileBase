package vn.shb.core.core.domain.source.response

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.data.entities.login.UserInfo

class LoginResponse : BaseResponse<UserInfo>()

class LogoutResponse : BaseResponse<ActionDone>()
