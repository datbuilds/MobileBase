package vn.shb.cam.activity

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountdownViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private val _stateResendTimer = MutableStateFlow<Int?>(null)
    val stateResendTimer = _stateResendTimer.asStateFlow()

    fun startCountDown(downTime: Long = 30000) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(downTime, 1000) {
            override fun onFinish() {
                _stateResendTimer.value = 0
            }

            override fun onTick(timeless: Long) {
                _stateResendTimer.value = (timeless / 1000).toInt()
            }
        }.start()
    }

    fun stopCountdown() {
        _stateResendTimer.value = null
        countDownTimer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopCountdown()
    }
}