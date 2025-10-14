package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.databinding.CustomDialogLayoutBinding
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.invisible
import vn.shb.lao.utils.extensions.visible
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
        isClose: Boolean = false,
        supView: View? = null,
        isCancelable: Boolean = true
    ) {
        val context = contextRef.get() ?: return
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogSlideAnimation)
        dialog?.setContentView(bindingView.root)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setDimAmount(0.5f)
        dialog?.setCancelable(isCancelable)
        dialog?.setOnDismissListener {
            dialog = null
        }

        bindingView.apply {

            if (isClose) {
                ivCloseDialog.visible()
                ivCloseDialog.setOnSingleClickListener { dismiss() }
            } else {
                ivCloseDialog.invisible()
            }

            if (supView != null) {
                flSupView.removeAllViews()
                flSupView.addView(supView)
                tvContentAlert.gone()
            } else {
                flSupView.gone()
            }

            tvTitleAlert.text =
                title ?: context.getString(R.string.notification)
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
            if (textNegative.isEmpty() && textPositive.isEmpty()) {
                llButton.gone()
            }
        }

        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}