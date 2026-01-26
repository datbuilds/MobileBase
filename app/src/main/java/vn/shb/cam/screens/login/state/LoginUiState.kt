package vn.shb.cam.screens.login.state

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.login.StateLogin

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Error(val reason: Reason) : LoginUiState()
    data class Success(val state: StateLogin) : LoginUiState()
}

sealed class LogoutUiState {
    object Idle : LogoutUiState()
    object Loading : LogoutUiState()
    data class Error(val reason: AppReason) : LogoutUiState()
    data class Success(val state: ActionDone) : LogoutUiState()
}