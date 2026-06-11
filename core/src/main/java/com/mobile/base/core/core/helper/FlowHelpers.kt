package com.mobile.base.core.core.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import java.util.Timer
import java.util.TimerTask

fun <T> Flow<T>.asCommonFlow(): CFlow<T> = CFlow(this)
class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit) {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))
    }
}

fun tickFlow(millis: Long = 1000L) = callbackFlow<Int> {
    val timer = Timer()
    var time = 0
    timer.schedule(
        object : TimerTask() {
            override fun run() {
                try {
                    trySend(time)
                } catch (e: Exception) {
                }
                time += 1
            }
        },
        0,
        millis
    )
    awaitClose {
        timer.cancel()
    }
}

fun <T, R> zip(
    vararg flows: Flow<T>,
    transform: suspend (List<T>) -> R
): Flow<R> = when (flows.size) {
    0 -> error("No flows")
    1 -> flows[0].map { transform(listOf(it)) }
    2 -> flows[0].zip(flows[1]) { a, b -> transform(listOf(a, b)) }
    else -> {
        var accFlow: Flow<List<T>> = flows[0].zip(flows[1]) { a, b -> listOf(a, b) }
        for (i in 2 until flows.size) {
            accFlow = accFlow.zip(flows[i]) { list, it ->
                list + it
            }
        }
        accFlow.map(transform)
    }
}