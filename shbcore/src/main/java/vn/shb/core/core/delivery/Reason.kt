package vn.shb.core.core.delivery

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class Reason : Throwable() {
    abstract val errMessage: String
    open val errorCode: String = "l"

    override fun toString(): String = errMessage

    fun getErrorMessage(): String = errMessage
}

fun Throwable.toReason(): Reason =
    when (this) {
        is SocketTimeoutException,
        is ConnectException,
        is UnknownHostException -> ConnectionError()

        else -> this.message?.let { GenericError(it) } ?: GenericError()
    }

// region CommonErrorDefinitions
object ReasonDescription {
    const val BAD_GATEWAY = "E502: Kết nối không thành công.  Vui lòng liên hệ với bộ phận hỗ trợ kỹ thuật!"
    const val NETWORK = "Kết nối mạng không khả dụng"
    const val EMPTY = "Không tìm thấy dữ liệu"
    const val RESPONSE = "Phản hồi từ máy chủ không hợp lệ"
    const val TIMEOUT = "Yêu cầu đã hết thời gian chờ"
    const val NOT_FOUND = "Không tìm thấy tài nguyên"
    const val UNAUTHORIZED = "Bạn không có quyền truy cập"
    const val BAD_REQUEST = "Yêu cầu không hợp lệ"
}

const val ERROR_CODE_DEFAULT = "Lỗi không xác định"

class GenericError(
    override val errMessage: String = ERROR_CODE_DEFAULT,
) : Reason()

sealed class NetworkError(
    override val errMessage: String
) : Reason()

class BadGatewayError : NetworkError(ReasonDescription.BAD_GATEWAY)
class ConnectionError : NetworkError(ReasonDescription.NETWORK)
class ResponseError : NetworkError(ReasonDescription.RESPONSE)
class EmptyResultError : NetworkError(ReasonDescription.EMPTY)
class TimeoutError : NetworkError(ReasonDescription.TIMEOUT)
class NotFoundError : NetworkError(ReasonDescription.NOT_FOUND)
class UnAuthorizedError(error: String = ReasonDescription.UNAUTHORIZED) : NetworkError(error)
class BadRequestError(error: String = ReasonDescription.BAD_REQUEST) : NetworkError(error)
// endregion
