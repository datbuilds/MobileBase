package vn.shb.core.core.domain.source.response

import vn.shb.core.core.delivery.BaseResponse

class SystemVarResponse : BaseResponse<SystemVarData>()

data class SystemVarData(
    val name: String? = null,
    val status: String? = null,
    val flag: String? = null,
    val description: String = "",
    val lastChange: String? = null,
    val active: Boolean? = false
) {
    fun isNeedUpdate(versionName: String): Boolean {
        if (!flag.equals("TRUE", true)) return false
        return versionName !in description.split(";")
    }
}
