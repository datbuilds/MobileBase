package vn.shb.cam.screens.login.ui.widget

import android.content.Context
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import vn.shb.cam.R
import vn.shb.cam.databinding.DialogConfirmDeviceBinding
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.utils.extesions.setOnSingleClickListener

class ConfirmDeviceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val binding: DialogConfirmDeviceBinding
    private var timer: CountDownTimer? = null
    private var onConfirmCallback: ((String) -> Unit)? = null
    private var onResendCallback: (() -> Unit)? = null
    private var onCloseCallback: (() -> Unit)? = null
    private var onFinishCallBack: (() -> Unit)? = null

    init {
        binding = DialogConfirmDeviceBinding.inflate(LayoutInflater.from(context), this, true)
        setupListeners()
    }

    fun setup(
        phoneNumber: String,
        totalTime: Long?,
        onConfirm: (String) -> Unit,
        resendCode: () -> Unit,
        onFinishCB: () -> Unit,
        onClose: () -> Unit
    ) {
        this.onConfirmCallback = onConfirm
        this.onResendCallback = resendCode
        this.onCloseCallback = onClose
        this.onFinishCallBack = onFinishCB

        val message = context.getString(R.string.confirm_device_message, phoneNumber)
        val spannable = SpannableString(message)
        val startIndex = message.indexOf(phoneNumber)
        if (startIndex != -1) {
            spannable.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                startIndex,
                startIndex + phoneNumber.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvMessage.text = spannable
        binding.tvError.gone()

        startTimer(totalTime ?: 60000L)
    }

    private fun setupListeners() {
        with(binding) {
            ivClose.setOnSingleClickListener {
                hide()
                onCloseCallback?.invoke()
            }

            btnClose.setOnSingleClickListener {
                hide()
                onCloseCallback?.invoke()
            }

            btnResend.setOnSingleClickListener {
                onResendCallback?.invoke()
                showResendCodeDialog(false)
                startTimer(60000L)
            }

//            otpView.setOtpCompleteListener { otp ->
//                onConfirmCallback?.invoke(otp)
////                hideSoftKeyboard()
//            }

            otpView.setOnOtpChangedListener { otp, isComplete ->
                btnConfirm.isEnabled = isComplete
                btnConfirm.alpha = if (isComplete) 1f else 0.5f
            }

            // Initial state
            btnConfirm.isEnabled = false
            btnConfirm.alpha = 0.5f

            btnConfirm.setOnSingleClickListener {
                val otp = otpView.getOtp()
                if (otp.length == 6) {
                    onConfirmCallback?.invoke(otp)
                }
            }
        }
    }

    private fun startTimer(totalTime: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(totalTime, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val timeString = context.getString(R.string.remaining_time, seconds.toString())
                val spannableTime = SpannableString(timeString)
                val index = timeString.indexOf(seconds.toString())
                if (index != -1) {
                    spannableTime.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        index,
                        index + seconds.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                binding.tvTimer.text = spannableTime
            }

            override fun onFinish() {
                binding.tvTimer.text = context.getString(R.string.remaining_time, "0")
                showResendCodeDialog(true)
                onFinishCallBack?.invoke()
            }
        }.start()
    }

    fun showErrorInvalidOtp(message: String) {
        with(binding) {
            tvError.text = message
            tvError.visible()
        }
    }

    private fun showResendCodeDialog(isShown: Boolean) {
        with(binding) {
            tvTimer.isVisible = !isShown
            tvError.isVisible = isShown
            tvError.text =
                if (isShown) context.getString(R.string.theOtpExpired)
                else context.getString(R.string.otp_incorrect_message)
            btnConfirm.isVisible = !isShown
            llResendCode.isVisible = isShown
            otpView.clearOtp()
        }
    }

    fun show() {
        visibility = View.VISIBLE
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        startAnimation(slideUp)
    }

    fun hide() {
        val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDown.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                visibility = View.GONE
                timer?.cancel()
                binding.otpView.clearOtp()
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
        startAnimation(slideDown)
    }

    fun clearOtp() {
        binding.otpView.clearOtp()
        binding.tvError.visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer?.cancel()
    }
}
