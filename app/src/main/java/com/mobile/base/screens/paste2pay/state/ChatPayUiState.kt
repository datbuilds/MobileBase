package com.mobile.base.screens.paste2pay.state

import com.mobile.base.core.core.delivery.Reason
import com.mobile.base.core.core.domain.source.response.AiPayResult

sealed interface ChatPayUiState {
    object Idle : ChatPayUiState
    object Loading : ChatPayUiState
    data class Success(val result: AiPayResult) : ChatPayUiState
    data class Error(val reason: Reason) : ChatPayUiState
}
