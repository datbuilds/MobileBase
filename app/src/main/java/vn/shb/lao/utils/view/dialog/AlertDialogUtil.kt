package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.ui.databinding.CustomDialogLayoutBinding

object AlertDialogUtil {
    private var alertDialog: AlertDialog? = null

    fun message(
        context: Context,
        title: String? = null,
        message: String,
        tvPositive: String = "",
        textNegative: String = "",
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))

        val builder =
            AlertDialog.Builder(context, R.style.dialog_transparent_width)
        builder.setView(bindingView.root)
        bindingView.apply {
            tvTitleAlert.text = title ?: context.getString(vn.shb.lao.localization.R.string.notification_channel_id)
            tvContentAlert.text = message
            buttonPositive.apply {
                text = tvPositive
                setOnSingleClickListener {
                    positiveAction.invoke()
                    alertDialog?.dismiss()
                }
            }

            buttonNegative.apply {
                text = textNegative
                setOnSingleClickListener {
                    negativeAction.invoke()
                    alertDialog?.dismiss()
                }
            }
        }

        alertDialog = builder.create()
        alertDialog?.window?.attributes?.windowAnimations = R.style.SlideInCenterAnimation
        alertDialog?.show()
    }
}