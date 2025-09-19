package vn.shb.lao.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.launchWhenResumed(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycleScope.launchWhenResumed {
        this@launchWhenResumed.collect()
    }
}

fun <T> Flow<T>.launchWhenStarted(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}