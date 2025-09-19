package vn.shb.lao.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

/**
 * Reduces required boilerplate code to observe a live data
 *
 * @param data [LiveData] to observe
 * @param block receive and process data
 */
fun <T> Fragment.observe(data: LiveData<T>, block: (T) -> Unit) {
    data.observe(this, Observer(block))
}

/**
 * collectState (default)
 *
 * collectDistinctState (chỉ collect khi giá trị mới khác giá trị cũ)
 *
 * collectDebouncedState (dùng cho search input, debounce)
 *
 * collectEvent (cho SharedFlow event)
 *
 * safeCollect (có xử lý lỗi)
 */

fun <T> Fragment.collectState(flow: Flow<T>, collector: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            flow.collectLatest {
                collector(it)
            }
        }
    }
}


fun LifecycleOwner.launchRepeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.CREATED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}

/**
 * Extension dành cho Activity:
 */
fun <T> AppCompatActivity.collectState(flow: Flow<T>, collector: suspend (T) -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest {
                collector(it)
            }
        }
    }
}

/**
 * Extension dành cho SharedFlow (không replay):
 */
fun <T> Fragment.collectEvent(flow: SharedFlow<T>, collector: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                collector(it)
            }
        }
    }
}

/**
 * Extension có thêm collect debounce
 */
fun <T> Fragment.collectDebouncedState(
    flow: Flow<T>,
    debounceTime: Long = 300L,
    collector: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.debounce(debounceTime).collectLatest {
                collector(it)
            }
        }
    }
}

/**
 * Extension có thêm collect debounce
 */
fun <T> AppCompatActivity.collectDebouncedState(
    flow: Flow<T>,
    debounceTime: Long = 300L,
    collector: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.debounce(debounceTime).collectLatest {
                collector(it)
            }
        }
    }
}

/**
 * Extension collect lỗi với try-catch tự động:
 */
fun <T> Fragment.safeCollect(
    flow: Flow<T>,
    onError: (Throwable) -> Unit = {},
    collector: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.catch { onError(it) }
                .collectLatest { collector(it) }
        }
    }
}

/**
 * Extension collect StateFlow chỉ collect khi giá trị thay đổi (distictUntilChanged):
 */
fun <T> Fragment.collectDistinctState(flow: StateFlow<T>, collector: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.distinctUntilChangedBy {
                it
            }.collectLatest {
                collector(it)
            }
        }
    }
}
