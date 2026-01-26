package vn.shb.cam.screens.splash.state

sealed class SplashUiState {
    object Idle : SplashUiState()
    object Loading : SplashUiState()
    data class Error(val reason: String) : SplashUiState()
}