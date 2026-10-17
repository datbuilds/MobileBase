package com.mobile.screens.home

import com.mobile.base.BaseViewModel
import com.mobile.core.core.delivery.Reason
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class HomeViewModel : BaseViewModel() {
    private val errors = Channel<Reason>(Channel.BUFFERED)
    val stateError = errors.receiveAsFlow()
    private val loading = MutableStateFlow(false)
    val stateLoading = loading.asStateFlow()

    fun resetLoadingState() {
        loading.value = false
    }
}
