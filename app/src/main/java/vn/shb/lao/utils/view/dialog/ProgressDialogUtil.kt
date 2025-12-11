package vn.shb.lao.utils.view.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import vn.shb.lao.R

object ProgressDialogUtil {
    private var alertDialog: AlertDialog? = null

    fun checkShow(context: Context){
        if ((alertDialog?.context as? Activity)?.isDestroyed == true) return
        if (alertDialog?.isShowing != true) {
            show(context)
        }
    }

    fun show(
        context: Context,
        isCancelable: Boolean = false,
        onDismissListener: (() -> Unit?)? = null,
    ) {
        if (context is Activity && !context.isFinishing) {
            dismiss()

            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.custom_loading, null)

//            val ivPlaying = view.findViewById<LottieAnimationView>(R.id.ivLoading)
//            ivPlaying.setAnimation("loading.lottie")
//            ivPlaying.repeatCount = LottieDrawable.INFINITE
//            ivPlaying.playAnimation()

            builder.setView(view)
            builder.setCancelable(isCancelable)
            alertDialog = builder.create()
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog?.setOnDismissListener {
                onDismissListener?.invoke()
            }
            alertDialog?.show()
        }
    }

    fun dismiss() {
        try {
            if ((alertDialog?.context as? Activity)?.isDestroyed == true) return
            if (alertDialog?.isShowing == true) {
                alertDialog?.dismiss()
                alertDialog = null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}