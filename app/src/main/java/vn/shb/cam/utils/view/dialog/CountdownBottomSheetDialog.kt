package vn.shb.cam.utils.view.dialog

import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.CustomDialogLayoutBinding
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.utils.extesions.setOnSingleClickListener

class CountdownBottomSheetDialog(
    private val message: Int,
    private val remainingSeconds: Int,
    private val onDismiss: (() -> Unit)? = null
) : BaseBottomDialogBinding<CustomDialogLayoutBinding>(CustomDialogLayoutBinding::inflate) {

    private var timer: CountDownTimer? = null

    override fun initView(view: View) {
        with(binding) {
            tvTitleAlert.text = getString(R.string.notification)
            ivCloseDialog.gone()
            
            buttonNegative.gone()
            viewCenter.gone()
            buttonPositive.apply {
                visible()
                text = getString(R.string.close)
                setOnSingleClickListener {
                    dismiss()
                }
            }
            
            // Start countdown
            startTimer()
        }
        
        // Ensure dialog is not cancelable
        isCancelable = false
    }

    override fun initListener() {
        // Listeners already set in initView
    }

    override fun initObserve() {
        // No observation needed
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(remainingSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (view == null) return
                
                val m = (millisUntilFinished / 1000) / 60
                val s = (millisUntilFinished / 1000) % 60
                val formatted = String.format("%02d:%02d", m, s)
                
                val fullText = getString(message, formatted)

                val spannable = SpannableString(fullText)
                val start = fullText.lastIndexOf(formatted)
                if (start != -1) {
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        start + formatted.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                binding.tvContentAlert.text = spannable
            }

            override fun onFinish() {
                if (view == null) return
                binding.tvContentAlert.text = getString(message, 0)
                dismiss()
                onDismiss?.invoke()
            }
        }.start()
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
    }

    companion object {
        const val TAG = "CountdownBottomSheetDialog"
    }
}
