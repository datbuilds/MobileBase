package vn.shb.cam.utils.view.dialog

import android.view.View
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogRegisterDeviceBinding
import vn.shb.core.utils.extesions.setOnSingleClickListener

class RegisterDeviceDialog(
    private val phoneNumber: String,
    private val isNewDevice: Boolean,
    private val onConfirm: () -> Unit,
    private val onCancel: () -> Unit
) : BaseBottomDialogBinding<DialogRegisterDeviceBinding>(DialogRegisterDeviceBinding::inflate) {

    override fun initView(view: View) {
        val message = getString(
            if (isNewDevice) R.string.newDeviceVerificial else R.string.register_device_message,
            phoneNumber
        )
        val spannable = android.text.SpannableString(message)
        val startIndex = message.indexOf(phoneNumber)
        if (startIndex != -1) {
            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                startIndex,
                startIndex + phoneNumber.length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvMessage.text = spannable

        // Ensure dialog is not cancelable by touching outside or back button
        isCancelable = false
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }

        binding.btnNo.setOnSingleClickListener {
            dismiss()
            onCancel()
        }

        binding.btnYes.setOnSingleClickListener {
            dismiss()
            onConfirm()
        }
    }

    override fun initObserve() {
        // No observation needed
    }

    companion object {
        const val TAG = "RegisterDeviceDialog"
    }
}
