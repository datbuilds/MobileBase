package vn.shb.lao.utils.extensions

import android.os.CountDownTimer
import android.util.Log

class CustomCountdownTimer(
    private val totalTimeMillis: Long,
    private val intervalMillis: Long,
    private val onTickAction: ((remainingTimeMillis: Long) -> Unit)? = null,
    private val onFinishAction: () -> Unit
) {
    private var timer: CountDownTimer? = null

    private var isRunning = false
    private var remainingTimeMillis = totalTimeMillis

    fun start() {
        if (!isRunning) {
            timer = object : CountDownTimer(remainingTimeMillis, intervalMillis) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTimeMillis = millisUntilFinished
                    onTickAction?.invoke(millisUntilFinished)
                }

                override fun onFinish() {
                    remainingTimeMillis = 0
                    isRunning = false
                    onFinishAction.invoke()
                }
            }

            timer?.start()
            isRunning = true
        }
    }

    fun stop() {
        timer?.cancel()
        isRunning = false
    }

    fun reset() {
        stop()
        remainingTimeMillis = totalTimeMillis
    }
}