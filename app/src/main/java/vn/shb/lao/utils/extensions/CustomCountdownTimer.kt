package vn.shb.lao.utils.extensions

import android.os.CountDownTimer

class CustomCountdownTimer(
    private val totalTimeMillis: Long,
    private val intervalMillis: Long,
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
                    // Thực hiện hành động sau mỗi tick (nếu cần)
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