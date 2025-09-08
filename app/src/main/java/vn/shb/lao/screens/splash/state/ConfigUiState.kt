package vn.shb.lao.screens.splash.state

sealed class ConfigUiState {
    object Idle : ConfigUiState()

    object Loading : ConfigUiState()

    data class Error(val reason: String) : ConfigUiState()

    data class Success(val state: Any) : ConfigUiState()
}
