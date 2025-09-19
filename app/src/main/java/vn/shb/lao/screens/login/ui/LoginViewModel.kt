package vn.shb.lao.screens.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.screens.login.state.LogoutUiState

class LoginViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseLogin: UseCaseLogin,
    private val useCaseLogout: UseCaseLogout
) : BaseViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val stateLogin = _state.asStateFlow()

    private val _stateLogout = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val stateLogout = _stateLogout.asStateFlow()

    fun login(param: UseCaseLogin.Params) {
        viewModelScope.launch {
            useCaseLogin(param).collect {
                it.onResultHandle(loadingBlock = {
                    _state.value = LoginUiState.Loading
                }, failureBlock = { reason ->
                    _state.value = LoginUiState.Error(reason as AppReason)
                }, successBlock = { state ->
                    _state.value = LoginUiState.Success(state)
                })
            }
        }
    }

    fun logout(param: UseCaseLogout.Params) {
        viewModelScope.launch {
            useCaseLogout(param).collect {
                it.onResultHandle(loadingBlock = {
                    _stateLogout.value = LogoutUiState.Loading
                }, failureBlock = { reason ->
                    _stateLogout.value = LogoutUiState.Error(reason as AppReason)
                }, successBlock = { state ->
                    _stateLogout.value = LogoutUiState.Success(state)
                })
            }
        }
    }

    fun clearLoginState() {
        _state.value = LoginUiState.Idle
    }

    //endregion
    override fun onCleared() {
        super.onCleared()
    }
}
