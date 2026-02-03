package vn.shb.cam.utils.view.dialog

import android.content.Context
import android.content.IntentFilter
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.auth.api.phone.SmsRetriever
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogConfirmDeviceBinding
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.widgets.SmsReceiver

class ConfirmDeviceDialog(
    private val phoneNumber: String,
    private val onConfirm: (String) -> Unit,
    private val resendCode : () -> Unit
) : BaseBottomDialogBinding<DialogConfirmDeviceBinding>(DialogConfirmDeviceBinding::inflate) {

    private var timer: CountDownTimer? = null
    private val totalTime = 60000L // 60 seconds
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
            // call API verify OTP
        }

        binding.btnConfirm.setOnSingleClickListener {
            val otp = binding.otpView.getOtp()
            if (otp.length == 6) {
                // Determine logic for validation.
                // For now, assume it's valid if length is 6, or mock validation.
                // If invalid -> show error
                // If valid -> callback
                onConfirm(otp)
                dismiss()
            }
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(totalTime, interval) {
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

    private fun showResendCodeDialog(isShown: Boolean) {
        with(binding){
            tvTimer.isVisible = !isShown
            tvError.isVisible = isShown
            tvError.text = if (isShown) getString(R.string.theOtpExpired) else getString(R.string.otp_incorrect_message)
            binding.btnConfirm.isVisible = !isShown
            binding.llResendCode.isVisible = isShown
        }
    }
    
    fun showErrorMessage() {
        binding.tvError.visible()
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
