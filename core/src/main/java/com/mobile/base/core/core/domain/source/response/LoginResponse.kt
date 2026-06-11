package com.mobile.base.core.core.domain.source.response

import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.data.entities.login.UserLog

class LoginResponse : BaseResponse<UserLog>()

class LogoutResponse : BaseResponse<ActionDone>()
