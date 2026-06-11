package com.mobile.base.utils.view.dialog

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.base.R
import com.mobile.base.base.view.MyTextView
import com.mobile.base.databinding.CustomDialogLayoutBinding
import com.mobile.base.databinding.DialogSystemErrorBinding
import com.mobile.base.utils.extensions.CustomCountdownTimer
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.invisible
import com.mobile.base.utils.extensions.visible
import com.mobile.base.core.core.delivery.Reason
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BottomSheetDialogHelper(context: Context) {

    private val contextRef = WeakReference(context)
    private var dialog: BottomSheetDialog? = null

    private fun isActivityValid(): Boolean {
        val context = contextRef.get() ?: return false
        if (context is Activity) {
            return !context.isFinishing && !context.isDestroyed
        }
        return true
    }

    fun messageLoginFail(
        messageError: String = "",
        lockedUntil: String = "",
        countFailed: Int
    ) {
        if (!isActivityValid()) return
        val context = contextRef.get() ?: return
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable = false)
        bindingView.bindView(
            context = context,
            title = context.getString(R.string.notification),
            message = messageError,
            textPositive = context.getString(R.string.close),
            isClose = false
        )
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val unlockTime = LocalDateTime.parse(lockedUntil, formatter)
        val zoneId = ZoneId.systemDefault()

        val unlockMillis = unlockTime.atZone(zoneId).toInstant().toEpochMilli()
        val currentMillis = System.currentTimeMillis()

        val remainingMillis = unlockMillis - currentMillis
        bindingView.tvContentAlert.countdownAndDismiss(messageError, remainingMillis, countFailed) {
            dismiss()
        }
        dialog?.show()
    }

    fun message(
        title: String? = null,
        message: String = "",
        textPositive: String = "",
        textNegative: String = "",
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        isClose: Boolean = false,
        supView: View? = null,
        isCancelable: Boolean = true,
        onDismiss: (() -> Unit)? = null
    ) {
        if (!isActivityValid()) return
        val context = contextRef.get() ?: return
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable, onDismiss)

        bindingView.bindView(
            context = context,
            title = title,
            message = message,
            textPositive = textPositive,
            textNegative = textNegative,
            positiveAction = positiveAction,
            negativeAction = negativeAction,
            isClose = isClose,
            supView = supView
        )

        dialog?.show()
    }

    fun messageErrorCode(
        reason: Reason,
        isCancelable: Boolean = false,
        close: () -> Unit = {}
    ) {
        if (!isActivityValid()) return
        val context = contextRef.get() ?: return
        val bindingView = DialogSystemErrorBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable)

        bindingView.tvErrorCode.text = reason.errorCode
        bindingView.tvContentAlert.text = reason.errMessage
        bindingView.tvClose.setOnSingleClickListener {
            close.invoke()
            dismiss()
        }
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog?.show()
    }

    private fun createDialog(
        context: Context,
        bindingView: ViewBinding,
        isCancelable: Boolean = false,
        onDismiss: (() -> Unit)? = null
    ) {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogSlideAnimation)
        dialog?.setContentView(bindingView.root)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setDimAmount(0.5f)
        dialog?.setCancelable(isCancelable)
//        dialog?.setCanceledOnTouchOutside(isCancelable)
        dialog?.setOnDismissListener {
            dialog = null
        }

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Xử lý khi bấm Back
                // return true để CHẶN dismiss
                true
            } else {
                false
            }
        }

        dialog?.setOnDismissListener {
            onDismiss?.invoke()
        }
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    private fun CustomDialogLayoutBinding.bindView(
        context: Context,
        title: String? = "",
        message: String = "",
        textPositive: String = "",
        textNegative: String = "",
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        isClose: Boolean = false,
        supView: View? = null,
    ) {

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

    fun MyTextView.countdownAndDismiss(
        message: String,
        timeLock: Long,
        countFailed: Int,
        onDismiss: (() -> Unit)? = null
    ) {

        val countdownTimer = CustomCountdownTimer(
            totalTimeMillis = timeLock,
            intervalMillis = 1000L,
            onTickAction = { millisUntilFinished ->
                val m = (millisUntilFinished / 1000) / 60
                val s = (millisUntilFinished / 1000) % 60
                val formatted = String.format("%02d:%02d", m, s)

                val message =
                    context.getString(R.string.loginFailed5Times, countFailed.toString(), formatted)
                val spannable = SpannableString(message)
                val start = message.indexOf(formatted)
                val end = start + formatted.length
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                this.text = spannable
            },
            onFinishAction = {
                onDismiss?.invoke()
            }
        )

        countdownTimer.start()
    }

}