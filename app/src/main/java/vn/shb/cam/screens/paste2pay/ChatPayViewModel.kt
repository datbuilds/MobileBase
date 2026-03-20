package vn.shb.cam.screens.paste2pay

import android.util.Base64
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.shb.cam.base.BaseViewModel
import vn.shb.cam.screens.paste2pay.state.ChatPayUiState
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.chatpay.UseCaseAiPay
import vn.shb.dn.choosePhotoHelper.utils.modifyOrientationAndResize
import java.io.File

class ChatPayViewModel(
    private val useCaseAiPay: UseCaseAiPay
) : BaseViewModel() {

    private val _state = MutableStateFlow<ChatPayUiState>(ChatPayUiState.Idle)
    val state = _state.asStateFlow()

    fun submitAiPay(
        textContent: String,
        imagePath: String?,
        requestUser: String
    ) {
        viewModelScope.launch {
            val params = runCatching {
                withContext(Dispatchers.IO) {
                    buildParams(
                        textContent = textContent,
                        imagePath = imagePath,
                        requestUser = requestUser
                    )
                }
            }.getOrElse { throwable ->
                _state.value = ChatPayUiState.Error(
                    reason = AppReason(message = throwable.message ?: "Không thể xử lý yêu cầu")
                )
                return@launch
            }

            useCaseAiPay(params).collect { result ->
                result.onResultHandle(
                    loadingBlock = {
                        _state.value = ChatPayUiState.Loading
                    },
                    failureBlock = { reason ->
                        _state.value = ChatPayUiState.Error(reason)
                    },
                    successBlock = { data ->
                        _state.value = ChatPayUiState.Success(data)
                    }
                )
            }
        }
    }

    fun resetState() {
        _state.value = ChatPayUiState.Idle
    }

    private fun buildParams(
        textContent: String,
        imagePath: String?,
        requestUser: String
    ): UseCaseAiPay.Params {
        val normalizedUser = requestUser.ifBlank { DEFAULT_REQUEST_USER }
        val normalizedText = textContent.trim()

        if (normalizedText.isNotEmpty()) {
            return UseCaseAiPay.Params(
                requestType = UseCaseAiPay.REQUEST_TYPE_TEXT,
                requestContent = normalizedText,
                requestUser = normalizedUser
            )
        }

        if (!imagePath.isNullOrBlank()) {
            val file = File(imagePath)
            if (!file.exists() || !file.isFile) {
                throw IllegalArgumentException("Ảnh đã chọn không tồn tại")
            }

            val compressedBytes = modifyOrientationAndResize(imagePath)
                ?: throw IllegalArgumentException("Không thể nén ảnh đã chọn")
            val base64Content = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
            return UseCaseAiPay.Params(
                requestType = UseCaseAiPay.REQUEST_TYPE_IMAGE,
                requestContent = base64Content,
                requestUser = normalizedUser
            )
        }

        throw IllegalArgumentException("Vui lòng nhập nội dung hoặc chọn ảnh")
    }

    companion object {
        private const val DEFAULT_REQUEST_USER = "admin"
    }
}
