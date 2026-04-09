package vn.shb.core.core.retrofit

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import vn.shb.core.core.delivery.BadRequestError
import vn.shb.core.core.delivery.ErrorResponse
import vn.shb.core.core.delivery.GenericError
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.ReasonDescription
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.TimeoutError
import vn.shb.core.core.delivery.UnAuthorizedError
import vn.shb.core.core.delivery.toReason

abstract class SafeExecute() {
    companion object {
        private const val TAG = "SafeExecute"

        // HTTP Status Code Constants for better readability
        private const val HTTP_BAD_REQUEST = 400
        const val HTTP_UNAUTHORIZED = 401
        private const val HTTP_FORBIDDEN = 403
        private const val HTTP_METHOD_NOT_ALLOWED = 405
        private const val HTTP_CONFLICT = 409
        private const val HTTP_GONE = 410
        private const val HTTP_UNSUPPORTED_MEDIA_TYPE = 415
        private const val HTTP_REQUEST_TIMEOUT = 408
        private const val HTTP_INTERNAL_SERVER_ERROR = 500
        private const val HTTP_BAD_GATEWAY = 502
        private const val HTTP_SERVICE_UNAVAILABLE = 503
        private const val HTTP_GATEWAY_TIMEOUT = 504

        const val HTTP_NOT_FOUND = "404"
        const val AUTH_006 = "AUTH-006"
        const val AUTH_001 = "AUTH-001"
        const val AUTH_002 = "AUTH-002"
        const val TOKEN_EXPIRE = "900901"
    }

    protected suspend fun <T : Any> execute(call: suspend () -> Response<T>): ResultSHB<T> {

        val response: Response<T>
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            t.printStackTrace()
            return ResultSHB.Failure(t.toReason())
        }

        if (response.isSuccessful) {
            if (response.body() != null) {
                return ResultSHB.Success(response.body()!!)
            }
        } else {
            val responseCode = response.code()
            val responseMessage = response.message()
            return ResultSHB.Failure(handleHttpError(responseCode, responseMessage, response))
        }
        return ResultSHB.Failure(GenericError())
    }

    /** Handle HTTP errors with proper categorization and error message extraction */
    private fun handleHttpError(
        responseCode: Int,
        responseMessage: String,
        response: Response<*>
    ): Reason {
        println("$TAG HTTP Error: $responseCode - $responseMessage")

        return when (responseCode) {
            // Client Errors (4xx)
//            HTTP_BAD_REQUEST -> {
//                println("$TAG Bad Request (400) - Validation error or invalid request")
//                createBadRequestError(response)
//            }

            HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> {
                println("$TAG Authentication Error ($responseCode) - Unauthorized or forbidden access")
                createUnauthorizedError(response, responseMessage)
            }

//            HTTP_NOT_FOUND -> {
//                println("$TAG Not Found (404) - Resource not found")
//                NotFoundError()
//            }
//
//            HTTP_REQUEST_TIMEOUT -> {
//                println("$TAG Request Timeout (408) - Client timeout")
//                TimeoutError()
//            }
//
//            HTTP_METHOD_NOT_ALLOWED -> {
//                println("$TAG Method Not Allowed (405) - HTTP method not supported")
//                createBadMethodError(response)
//            }
//
//            HTTP_CONFLICT -> {
//                println("$TAG Conflict (409) - Resource conflict")
//                createConflictError(response)
//            }
//
//            HTTP_GONE -> {
//                println("$TAG Gone (410) - Resource permanently deleted")
//                createGoneError(response)
//            }
//
//            HTTP_UNSUPPORTED_MEDIA_TYPE -> {
//                println("$TAG Unsupported Media Type (415) - Invalid content type")
//                createUnsupportedTypeError(response)
//            }

            // Server Errors (5xx)
//            HTTP_INTERNAL_SERVER_ERROR -> {
//                println("$TAG Internal Server Error (500) - Server error")
//                createInternalServerError(response)
//            }
//
//            HTTP_BAD_GATEWAY -> {
//                println("$TAG Bad Gateway (502) - Gateway error")
//                createBadGatewayError(response)
//            }
//
//            HTTP_SERVICE_UNAVAILABLE -> {
//                println("$TAG Service Unavailable (503) - Service temporarily unavailable")
//                createServiceUnavailableError(response)
//            }
//
//            HTTP_GATEWAY_TIMEOUT -> {
//                println("$TAG Gateway Timeout (504) - Gateway timeout")
//                createGatewayTimeoutError(response)
//            }

            // Other errors
            else -> {
                println("$TAG Unknown HTTP Error ($responseCode) - Unhandled status code")
                createGenericError(response, responseMessage)
            }
        }
    }

    /** Create BadRequestError (400) with server message if available */
    private fun createBadRequestError(response: Response<*>): BadRequestError {
        val serverMessage = extractServerMessage(response)
        return if (serverMessage.isNotEmpty()) {
            BadRequestError(serverMessage)
        } else {
            BadRequestError()
        }
    }

    /** Create UnAuthorizedError (401/403) with server message if available */
    private fun createUnauthorizedError(
        response: Response<*>,
        defaultMessage: String
    ): UnAuthorizedError {
        var code: String = ""
        val serverMessage = extractServerMessage(response) {
            code = it
        }
        return if (serverMessage.isNotEmpty()) {
            UnAuthorizedError(serverMessage, code)
        } else {
            UnAuthorizedError(defaultMessage, code)
        }
    }

    /** Create ConflictError (409) for resource conflicts */
    private fun createConflictError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(
            errMessage = serverMessage.ifEmpty { "Tài nguyên đã tồn tại hoặc xung đột" }
        )
    }

    /** Create GoneError (410) for permanently deleted resources */
    private fun createGoneError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(errMessage = serverMessage.ifEmpty { "Tài nguyên đã bị xóa vĩnh viễn" })
    }

    /** Create UnsupportedTypeError (415) for unsupported media types */
    private fun createUnsupportedTypeError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(
            errMessage = serverMessage.ifEmpty { "Định dạng dữ liệu không được hỗ trợ" }
        )
    }

    /** Create BadMethodError (405) for unsupported HTTP methods */
    private fun createBadMethodError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(
            errMessage = serverMessage.ifEmpty { "Phương thức HTTP không được hỗ trợ" }
        )
    }

    /** Create InternalServerError (500) with server message if available */
    private fun createInternalServerError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(errMessage = serverMessage.ifEmpty { "Lỗi máy chủ nội bộ" })
    }

    /** Create BadGatewayError (502) with server message if available */
    private fun createBadGatewayError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
//        return GenericError(errMessage = serverMessage.ifEmpty { ReasonDescription.BAD_GATEWAY })
        return GenericError(errMessage = ReasonDescription.BAD_GATEWAY)
    }

    /** Create ServiceUnavailableError (503) with server message if available */
    private fun createServiceUnavailableError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(
            errMessage = serverMessage.ifEmpty { "Dịch vụ tạm thời không khả dụng" }
        )
    }

    /** Create GatewayTimeoutError (504) with server message if available */
    private fun createGatewayTimeoutError(response: Response<*>): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(
            errMessage =
                serverMessage.ifEmpty {
                    "Gateway timeout - máy chủ trung gian hết thời gian chờ"
                }
        )
    }

    /** Create GenericError for unknown HTTP status codes */
    private fun createGenericError(response: Response<*>, defaultMessage: String): GenericError {
        val serverMessage = extractServerMessage(response)
        return GenericError(errMessage = serverMessage.ifEmpty { defaultMessage })
    }

    /** Extract server message from response body safely */
    private fun extractServerMessage(
        response: Response<*>,
        codeError: ((String) -> Unit)? = null
    ): String {
        return try {
            response.errorBody()?.let { errorBody ->
                val bodyString = errorBody.stringSuspending()
                if (bodyString.isNotEmpty()) {
                    try {
                        val errorResponse = Gson().fromJson(bodyString, ErrorResponse::class.java)
                        val message = errorResponse.message?.trim() ?: ""
                        val code = errorResponse.code?.trim() ?: ""
                        codeError?.invoke(code)
                        message
                    } catch (jsonException: Exception) {
                        println("SafeExecute -->  Failed to parse error response JSON: ${jsonException.message}")
                        println("SafeExecute -->  Raw error body: $bodyString")
                        bodyString.trim()
                    }
                } else {
                    println("Empty error response body")
                    ""
                }
            }
                ?: run {
                    println("SafeExecute --> No error body available")
                    ""
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("SafeExecute --> Error reading response body: ${ex.message}")
            ""
        }
    }

    /**
     * Safely read response body string with proper error handling Fixes IllegalStateException:
     * closed error
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private fun ResponseBody.stringSuspending(): String {
        return try {
            string()
        } catch (e: IllegalStateException) {
            println("SafeExecute --> Response body is closed: ${e.message}")
            ""
        } catch (e: Exception) {
            println("SafeExecute --> Error reading response body: ${e.message}")
            ""
        }
    }
}
