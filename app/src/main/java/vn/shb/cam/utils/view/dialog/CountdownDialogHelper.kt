package vn.shb.cam.utils.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import androidx.core.view.isVisible
import vn.shb.cam.R
import vn.shb.cam.databinding.CustomDialogLayoutBinding
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.utils.extesions.setOnSingleClickListener

class CountdownDialogHelper(private val context: Context) {

    private var dialog: AlertDialog? = null
    private var timer: CountDownTimer? = null

    fun show(
        message: String,
        remainingSeconds: Int,
        onDismiss: (() -> Unit)? = null
    ) {
        dismiss() // Dismiss any existing dialog

        val binding = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        
        // Setup Dialog
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        builder.setCancelable(false)
        
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Setup View
        setupView(binding, message, remainingSeconds, onDismiss)
        
        dialog?.show()
    }

    private fun setupView(
        binding: CustomDialogLayoutBinding,
        baseMessage: String,
        totalSeconds: Int,
        onDismiss: (() -> Unit)?
    ) {
        with(binding) {
            tvTitleAlert.text = context.getString(R.string.notification)
            ivCloseDialog.gone() // User screenshot has no top-right close X, only bottom button
            
            // Icon handling if needed. CustomDialogLayoutBinding might have an icon. 
            // BottomSheetDialogHelper doesn't seem to set it explicitly other than default?
            // Assuming default icon is (i) or we can set it if the binding exposes it.
            
            buttonNegative.gone()
            buttonPositive.apply {
                visible()
                text = context.getString(R.string.close)
                setOnSingleClickListener {
                    dismiss()
                    onDismiss?.invoke()
                }
            }
            
            // Start Timer
            startTimer(binding, baseMessage, totalSeconds)
        }
    }

    private fun startTimer(binding: CustomDialogLayoutBinding, baseMessage: String, totalSeconds: Int) {
        timer?.cancel()
        timer = object : CountDownTimer(totalSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val m = (millisUntilFinished / 1000) / 60
                val s = (millisUntilFinished / 1000) % 60
                val formatted = String.format("%02d:%02d", m, s)
                
                val fullMsg = "$baseMessage ${formatted}" 
                // OR better: "$baseMessage" and let the user pass a message that ends with "in"
                // Inspecting BottomSheetDialogHelper logic: 
                // "${baseMsg.plus(" ")}${context.getString(R.string.pleaseTryAgainIn).plus(Const.SEPARATOR_SPACE)}$formatted ${context.getString(R.string.minus)}."
                
                // I'll use the same logic as BottomSheetDialogHelper for consistency
                val pleaseTryAgain = context.getString(R.string.pleaseTryAgainIn)
                val fullText = "$baseMessage $pleaseTryAgain $formatted"

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
                binding.tvContentAlert.text = fullMsg
            }

            override fun onFinish() {
                binding.tvContentAlert.text = baseMessage // Or some finished text
                dismiss()
               // onDismiss?.invoke() // Maybe not invoke onDismiss automatically on finish? generic behavior is usually just close.
            }
        }.start()
    }

    fun dismiss() {
        timer?.cancel()
        dialog?.dismiss()
        dialog = null
        timer = null
    }
}
