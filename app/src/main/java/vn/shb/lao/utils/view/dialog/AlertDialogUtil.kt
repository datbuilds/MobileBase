package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.ui.databinding.CustomDialogLayoutBinding
import vn.shb.lao.utils.extensions.gone

object AlertDialogUtil {
    private var alertDialog: AlertDialog? = null

    fun message(
        context: Context,
        title: String? = null,
        message: String = "",
        idIcon: Int? = null,
        tvPositive: String = "",
        textNegative: String = "",
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        supView: View? = null
    ) {
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))

        val builder =
            AlertDialog.Builder(context, R.style.dialog_transparent_width)
        builder.setView(bindingView.root)
        builder.setCancelable(false)
        builder.setOnDismissListener {
            alertDialog = null
        }
        bindingView.apply {
            idIcon?.let { ivIconTop.setImageResource(it) }

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
            if (tvPositive.isEmpty()) {
                buttonPositive.gone()
                viewCenter.gone()
            } else {
                buttonPositive.apply {
                    isVisible = true
                    text = tvPositive
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

        alertDialog = builder.create()
        alertDialog?.window?.attributes?.windowAnimations = R.style.SlideInCenterAnimation
        alertDialog?.show()
    }

    fun dismiss() {
        alertDialog?.dismiss()
        alertDialog = null
    }
}