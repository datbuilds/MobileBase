package vn.shb.core.core.delivery.reason

import vn.shb.core.core.delivery.Reason

class LoginFailReason(
    override val message: String,
    val lockedUntil: String = ""

) : Reason() {
    override val errMessage: String
        get() = message

}