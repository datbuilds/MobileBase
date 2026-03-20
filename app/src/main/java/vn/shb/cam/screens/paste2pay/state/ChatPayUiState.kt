package vn.shb.cam.screens.paste2pay.state

import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.domain.source.response.AiPayResult

sealed interface ChatPayUiState {
    object Idle : ChatPayUiState
    object Loading : ChatPayUiState
    data class Success(val result: AiPayResult) : ChatPayUiState
    data class Error(val reason: Reason) : ChatPayUiState
}
