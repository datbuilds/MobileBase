package com.mobile.base.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.launchWhenResumed(lifecycleOwner: LifecycleOwner) {
    val flow = this
    lifecycleOwner.lifecycleScope.launchWhenResumed {
        flow.collect { }
    }
}

fun <T> Flow<T>.launchWhenStarted(lifecycleOwner: LifecycleOwner) {
    val flow = this
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        flow.collect { }
    }
}
