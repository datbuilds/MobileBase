package com.mobile.base.screens.login.state

import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.Reason
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.data.entities.login.UserLog

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Error(val reason: Reason) : LoginUiState()
    data class Success(val state: UserLog) : LoginUiState()
}

sealed class LogoutUiState {
    object Idle : LogoutUiState()
    object Loading : LogoutUiState()
    data class Error(val reason: AppReason) : LogoutUiState()
    data class Success(val state: ActionDone) : LogoutUiState()
}