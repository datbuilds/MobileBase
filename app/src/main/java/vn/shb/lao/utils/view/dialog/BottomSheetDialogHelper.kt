package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.databinding.CustomDialogLayoutBinding
import vn.shb.lao.utils.extensions.gone
import java.lang.ref.WeakReference

class BottomSheetDialogHelper(context: Context) {

    private val contextRef = WeakReference(context)
    private var dialog: BottomSheetDialog? = null

    fun message(
        title: String? = null,
        message: String = "",
        textPositive: String = "",
        textNegative: String = "",
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        supView: View? = null,
        isCancelable: Boolean = true
    ) {
        val context = contextRef.get() ?: return
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog = BottomSheetDialog(context, R.style.dialog_transparent_width)
        dialog?.setContentView(bindingView.root)
        dialog?.setCancelable(isCancelable)
        dialog?.setOnDismissListener {
            dialog = null
        }

        bindingView.apply {

            if (supView != null) {
                flSupView.removeAllViews()
                flSupView.addView(supView)
                tvContentAlert.gone()
            } else {
                flSupView.gone()
            }

            tvTitleAlert.text =
                title ?: context.getString(vn.shb.lao.localization.R.string.notification_channel_id)
            tvContentAlert.text = message

            if (textPositive.isEmpty()) {
                buttonPositive.gone()
                viewCenter.gone()
            } else {
                buttonPositive.apply {
                    isVisible = true
                    text = textPositive
                    setOnSingleClickListener {
                        positiveAction?.invoke()
                        dismiss()
                    }
                }
            }

            if (textNegative.isEmpty()) {
                buttonNegative.gone()
                viewCenter.gone()
            } else {
                buttonNegative.apply {
                    isVisible = true
                    text = textNegative
                    setOnSingleClickListener {
                        negativeAction?.invoke()
                        dismiss()
                    }
                }
            }
        }

        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}