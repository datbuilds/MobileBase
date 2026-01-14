package vn.shb.core.core.domain.source.response

import vn.shb.core.core.delivery.BaseResponse

class SystemVarResponse : BaseResponse<SystemVarData>()

data class SystemVarData(
    val name: String?,
    val status: String?,
    val flag: String?,
    val description: String?,
    val lastChange: String?,
    val active: Boolean?
)
