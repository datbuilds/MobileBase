package vn.shb.cam.screens.profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.domain.usecases.profile.UseCaseChangePassword
import vn.shb.cam.base.BaseViewModel

class ChangePasswordViewModel(
    private val useCaseChangePassword: UseCaseChangePassword
) : BaseViewModel() {

    private val _state = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val state = _state.asStateFlow()

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            useCaseChangePassword(UseCaseChangePassword.Params(oldPass, newPass)).collect {
                it.onResultHandle(
                    loadingBlock = {
                        _state.value = ChangePasswordState.Loading
                    },
                    failureBlock = { reason ->
                        _state.value = ChangePasswordState.Error(reason)
                    },
                    successBlock = { data ->
                        _state.value = ChangePasswordState.Success
                    }
                )
            }
        }
    }
    
    fun resetState() {
        _state.value = ChangePasswordState.Idle
    }
}

sealed interface ChangePasswordState {
    object Idle : ChangePasswordState
    object Loading : ChangePasswordState
    object Success : ChangePasswordState
    data class Error(val reason: Reason) : ChangePasswordState
}
