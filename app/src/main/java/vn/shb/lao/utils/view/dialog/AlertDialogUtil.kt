package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R

object AlertDialogUtil {
    private var alertDialog: AlertDialog? = null

    fun message(
        context: Context,
        title: String? = null,
        message: String,
        tvAction: String = "",
        textNegative: String = "",
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null)

        val builder =
            AlertDialog.Builder(context, R.style.dialog_transparent_width)
        builder.setView(dialogView)
        dialogView.rootView.findViewById<TextView>(R.id.tvAlertTitle).text = title ?: "Thông báo"
        dialogView.rootView.findViewById<TextView>(R.id.tvAlertContent).text = message
        dialogView.rootView.findViewById<MaterialButton>(R.id.btnAlertConfirm).apply {
            text = tvAction
            setTextColor(context.getColor(R.color.white))
            setOnSingleClickListener {
                positiveAction.invoke()
                alertDialog?.dismiss()
            }
        }
        builder.setNegativeButton(textNegative) { dialog, _ ->
            negativeAction.invoke()
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog?.window?.attributes?.windowAnimations = R.style.SlideInCenterAnimation
        alertDialog?.show()
    }
}