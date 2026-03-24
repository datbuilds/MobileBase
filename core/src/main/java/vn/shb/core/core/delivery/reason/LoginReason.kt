package vn.shb.core.core.delivery.reason

import vn.shb.core.core.delivery.Reason

class LoginFailLocked(
    override val message: String,
    val lockedUntil: String = ""

) : Reason() {
    override val errMessage: String
        get() = message

}

class LoginRegisterDevice(
    override val message: String,
    val masked_phone_number: String = "",
    val is_new_device: Boolean = false,
    override val errorCode : String

) : Reason() {
    override val errMessage: String
        get() = message

}

class RegisterDeviceError(
    override val message: String,
    val code: String,
    val remainingSeconds: Int? = null,
    val transactionId: String? = null
) : Reason() {
    override val errMessage: String
        get() = message
    override val errorCode: String
        get() = code
}
class VerifyOtpError(
    override val message: String,
    val code: String,
    val remainingSeconds: Int? = null,
    val maxAttempts: Int? = null
) : Reason() {
    override val errMessage: String
        get() = message
    override val errorCode: String
        get() = code
}