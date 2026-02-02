package vn.shb.cam.utils.view.dialog

import android.content.Context
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.core.widget.doAfterTextChanged
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogConfirmDeviceBinding
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.visible

class ConfirmDeviceDialog(
    private val phoneNumber: String,
    private val onConfirm: (String) -> Unit
) : BaseBottomDialogBinding<DialogConfirmDeviceBinding>(DialogConfirmDeviceBinding::inflate) {

    private var timer: CountDownTimer? = null
    private val totalTime = 60000L // 60 seconds
    private val interval = 1000L

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
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }

        binding.pinView.doAfterTextChanged {
            val otp = it.toString()
            binding.btnConfirm.isEnabled = otp.length == 6
            binding.btnConfirm.alpha = if (otp.length == 6) 1f else 0.5f
            
            // Hide error when user types
            binding.tvError.gone()
        }

        binding.btnConfirm.setOnSingleClickListener {
            val otp = binding.pinView.text.toString()
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
            }
        }.start()
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
