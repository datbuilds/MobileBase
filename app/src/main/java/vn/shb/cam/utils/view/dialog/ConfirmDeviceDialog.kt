package vn.shb.cam.utils.view.dialog

import android.content.IntentFilter
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.phone.SmsRetriever
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogConfirmDeviceBinding
import vn.shb.cam.utils.extensions.hideSoftKeyboard
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.widgets.SmsReceiver
import vn.shb.core.utils.extesions.setOnSingleClickListener

class ConfirmDeviceDialog(
    private val phoneNumber: String,
    private val totalTime: Long? = 60000L,
    private val onConfirm: (String) -> Unit,
    private val resendCode: () -> Unit
) : BaseBottomDialogBinding<DialogConfirmDeviceBinding>(DialogConfirmDeviceBinding::inflate) {

    private var timer: CountDownTimer? = null
    private val interval = 1000L

    private lateinit var smsReceiver: SmsReceiver

    override fun initView(view: View) {
        val message = getString(R.string.confirm_device_message, phoneNumber)
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

        // Ensure dialog is not cancelable by touching outside or back button
        isCancelable = false

        startTimer()
        smsReceiver = SmsReceiver { otp ->
            binding.otpView.setOtp(otp) // 🔥 auto fill
        }

        activity?.registerReceiver(
            smsReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        )
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }

        binding.btnClose.setOnSingleClickListener {
            dismiss()
        }

        binding.btnResend.setOnSingleClickListener {
            resendCode()
            showResendCodeDialog(false)
            startTimer()
        }

        binding.otpView.setOtpCompleteListener { otp ->
            onConfirm.invoke(otp)
            hideSoftKeyboard()
        }

        binding.otpView.setOnOtpChangedListener { otp, isComplete ->
            binding.btnConfirm.isEnabled = isComplete
            binding.btnConfirm.alpha = if (isComplete) 1f else 0.5f
        }

        // Initial state
        binding.btnConfirm.isEnabled = false
        binding.btnConfirm.alpha = 0.5f

        binding.btnConfirm.setOnSingleClickListener {
            val otp = binding.otpView.getOtp()
            if (otp.length == 6) {
                // Determine logic for validation.
                // For now, assume it's valid if length is 6, or mock validation.
                // If invalid -> show error
                // If valid -> callback
                onConfirm(otp)
            }
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(totalTime ?: 60000L, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val timeString = getString(R.string.remaining_time, seconds.toString())
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
                binding.tvTimer.text = getString(R.string.remaining_time, "0")
                // Handle timeout if needed
                showResendCodeDialog(true)
            }
        }.start()
    }

    fun showErrorInvalidOtp(message: String) {
        if (view == null) return
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
                if (isShown) getString(R.string.theOtpExpired) else getString(R.string.otp_incorrect_message)
            binding.btnConfirm.isVisible = !isShown
            binding.llResendCode.isVisible = isShown
            otpView.clearOtp()
        }
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
    }

    override fun initObserve() {
        // No observation needed
    }

    companion object {
        const val TAG = "ConfirmDeviceDialog"
    }
}
