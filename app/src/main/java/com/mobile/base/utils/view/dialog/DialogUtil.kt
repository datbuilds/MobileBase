package com.mobile.base.utils.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mobile.base.R

object DialogUtil {
    private var alertDialog: AlertDialog? = null

    fun message(context: Context, title: String?, message: String?, ok: String = "OK") {
        message(context, title, message, ok, null)
    }


    fun message(
        context: Context,
        title: String?,
        message: String?,
        ok: String?,
        callback: DialogInterface.OnClickListener
    ) {
        message(context, title, message, ok, null, callback, null)
    }

    fun message(
        context: Context,
        title: String? = "",
        message: String?,
        ok: String? = "",
        cancel: String? = "",
        okCallback: DialogInterface.OnClickListener? = null,
        cancelCallback: DialogInterface.OnClickListener? = null
    ) {
        dismiss()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.notice_dialog, null)
        val titleView: TextView = view.findViewById(R.id.textView_title)
        val messageView: TextView = view.findViewById(R.id.textView_message)
        val negativeButton: TextView = view.findViewById(R.id.button_negative)
        val positionButton: TextView = view.findViewById(R.id.button_positive)

        val builder = AlertDialog.Builder(context, R.style.dialog_transparent_width)
        builder.setView(view)

        title?.let {
            titleView.visibility = View.VISIBLE
            titleView.text = it
        } ?: run { titleView.visibility = View.GONE }

        message?.let {
            messageView.visibility = View.VISIBLE
            messageView.text = it
        } ?: run { messageView.visibility = View.GONE }

        ok?.let {
            positionButton.text = it
            positionButton.setOnClickListener {
                okCallback?.onClick(alertDialog, DialogInterface.BUTTON_POSITIVE)
                dismiss()
            }
        } ?: run {
            positionButton.visibility = View.GONE
        }

        cancel?.let {
            negativeButton.visibility = View.VISIBLE
            negativeButton.text = it
            negativeButton.setOnClickListener {
                cancelCallback?.onClick(alertDialog, DialogInterface.BUTTON_NEGATIVE)
                dismiss()
            }
        } ?: run {
            negativeButton.visibility = View.GONE
        }

        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog?.window?.attributes?.windowAnimations = R.style.SlideInCenterAnimation
        alertDialog?.setCanceledOnTouchOutside(false)
        alertDialog = builder.show()
    }

    fun showMessage(
        context: Context,
        title: String? = "",
        message: String?,
        ok: String? = "",
        cancel: String? = "",
        okCallback: DialogInterface.OnClickListener? = null,
        cancelCallback: DialogInterface.OnClickListener? = null
    ) {
        dismiss()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.notice_custom, null)
        val titleView: TextView = view.findViewById(R.id.textView_title)
        val messageView: TextView = view.findViewById(R.id.textView_message)
        val negativeButton: TextView =
            view.findViewById(R.id.button_negative_custom)
        val positionButton: TextView =
            view.findViewById(R.id.button_positive_custom)

        val builder =
            AlertDialog.Builder(context, R.style.dialog_transparent_width)
        builder.setView(view)

        if (title?.isEmpty() == true) {
            titleView.visibility = View.GONE
        } else {
            titleView.visibility = View.VISIBLE
        }

        message?.let {
            messageView.visibility = View.VISIBLE
            messageView.text = it
        } ?: run { messageView.visibility = View.GONE }

        ok?.let {
            positionButton.text = it
            positionButton.setOnClickListener {
                okCallback?.onClick(alertDialog, DialogInterface.BUTTON_POSITIVE)
                dismiss()
            }
        } ?: run {
            positionButton.visibility = View.GONE
        }

        cancel?.let {
            negativeButton.visibility = View.VISIBLE
            negativeButton.text = it
            negativeButton.setOnClickListener {
                cancelCallback?.onClick(alertDialog, DialogInterface.BUTTON_NEGATIVE)
                dismiss()
            }
        } ?: run {
            negativeButton.visibility = View.GONE
        }

        alertDialog = builder.show()
    }

    fun dismiss() {
        alertDialog?.dismiss()
        alertDialog = null
    }
}