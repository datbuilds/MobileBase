package vn.shb.lao.screens.splash.state

sealed class SplashUiState {
    object Idle : SplashUiState()
    object Loading : SplashUiState()
    data class Error(val reason: String) : SplashUiState()
}