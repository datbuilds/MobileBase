package vn.shb.core.core.delivery

import com.google.gson.annotations.SerializedName
import java.io.Serializable

abstract class BaseResponse<T>(
    @SerializedName("errorCode") val errorCode: String = "",
    @SerializedName("errorMessage") val errorMessage: String = "",
    @SerializedName("data") val data: T? = null,
//    @SerializedName("tranDate") val tranDate: String = "",
//    @SerializedName("totalElements") val totalElements: Int = 0,
) : Serializable {
    fun isSuccess() = errorCode.contentEquals("00")

    fun isOtherDevice() = errorCode.contentEquals("999")
}

fun <T> ResultSHB<BaseResponse<T>>.toResultData(preReturn: ((data: T) -> Unit)? = null): ResultSHB<T> {
    return when (this) {
        is ResultSHB.Success ->
            this.successData.toResult(preReturn)

        is ResultSHB.Failure -> this
        else -> ResultSHB.Loading
    }
}

fun <T> BaseResponse<T>.toResult(preReturn: ((data: T) -> Unit)? = null): ResultSHB<T> {
    return if (data != null) {
        preReturn?.invoke(data)
        ResultSHB.Success(data)
    } else
        ResultSHB.Failure(EmptyResultError())
}

class EmptyResponse : BaseResponse<ActionDone>()

fun ResultSHB<EmptyResponse>.toResultDataAction(preReturn: ((data: ActionDone) -> Unit)? = null) =
    when (this) {
        is ResultSHB.Success -> {
            preReturn?.invoke(ActionDone)
            ResultSHB.Success(ActionDone)
        }

        is ResultSHB.Failure -> this
        else -> ResultSHB.Loading
    }

data class ErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("errCode") val code: String? = null
)