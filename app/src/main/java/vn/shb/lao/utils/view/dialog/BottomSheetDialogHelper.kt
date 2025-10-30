package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.ConfirmCodeFragmentBinding
import vn.shb.lao.databinding.CustomDialogLayoutBinding
import vn.shb.lao.databinding.DialogSystemErrorBinding
import vn.shb.lao.utils.extensions.CustomCountdownTimer
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.invisible
import vn.shb.lao.utils.extensions.visible
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BottomSheetDialogHelper(context: Context) {

    private val contextRef = WeakReference(context)
    private var dialog: BottomSheetDialog? = null

    fun messageLoginFail(
        messageError: String = "",
        lockedUntil: String = ""
    ) {
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
        bindingView.tvContentAlert.countdownAndDismiss(messageError, remainingMillis) {
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
        isCancelable: Boolean = true
    ) {
        val context = contextRef.get() ?: return
        val bindingView = CustomDialogLayoutBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable)

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
        message: String = "",
        isCancelable: Boolean = false
    ) {
        val context = contextRef.get() ?: return
        val bindingView = DialogSystemErrorBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable)

        bindingView.tvContentAlert.text = message
        bindingView.tvClose.setOnSingleClickListener {
            dismiss()
        }

        dialog?.show()
    }

    fun showDialogConfirmCode(
        authSms: String,
        isCancelable: Boolean = true,
        actionConfirmCode: (String) -> Unit,
        actionDismiss: () -> Unit
    ) {
        val context = contextRef.get() ?: return
        val bindingView = ConfirmCodeFragmentBinding.inflate(LayoutInflater.from(context))
        createDialog(context, bindingView, isCancelable)

        with(bindingView) {
            tvContentAlert.text = context.getString(R.string.pleaseEnterConfirmCode, authSms)
            ivCloseDialog.setOnSingleClickListener { dismiss() }
            edtEnterCode.imeOptions = EditorInfo.IME_ACTION_DONE
        }

        bindingView.tvRemainingTime.countdownConfirmCode() {
            actionDismiss.invoke()
            dismiss()
        }

        bindingView.edtEnterCode.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                dismiss()
                actionConfirmCode.invoke(bindingView.edtEnterCode.text.toString())
                true
            } else {
                false
            }
        }

        bindingView.tvConfirm.setOnSingleClickListener {
            dismiss()
            actionConfirmCode.invoke(bindingView.edtEnterCode.text.toString())
        }

//        bindingView.edtEnterCode.doAfterTextChanged {
//            if ((it?.length ?: 0) >= 3) {
//                actionConfirmCode.invoke(it.toString())
//            }
//        }
        dialog?.show()
    }

    private fun createDialog(
        context: Context,
        bindingView: ViewBinding,
        isCancelable: Boolean = false
    ) {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogSlideAnimation)
        dialog?.setContentView(bindingView.root)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setDimAmount(0.5f)
        dialog?.setCancelable(isCancelable)
        dialog?.setOnDismissListener {
            dialog = null
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
        onDismiss: (() -> Unit)? = null
    ) {

        val countdownTimer = CustomCountdownTimer(
            totalTimeMillis = timeLock,
            intervalMillis = 1000L,
            onTickAction = { millisUntilFinished ->
                val m = (millisUntilFinished / 1000) / 60
                val s = (millisUntilFinished / 1000) % 60
                val formatted = String.format("%02d:%02d", m, s)

                val baseMsg = message
                val fullMsg = "${baseMsg.plus(" ")}${
                    context.getString(R.string.pleaseTryAgainIn).plus(" ")
                }$formatted"

                val spannable = SpannableString(fullMsg)
                val start = fullMsg.indexOf(formatted)
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

    fun MyTextView.countdownConfirmCode(
        timeLock: Long = 2,
        onDismiss: (() -> Unit)? = null
    ) {

        val countdownTimer = CustomCountdownTimer(
            totalTimeMillis = timeLock * 60 * 1000,
            intervalMillis = 1000L,
            onTickAction = { millisUntilFinished ->
                val s = millisUntilFinished / 1000
                val formatted = String.format("%02d", s)

                val fullMsg = "${context.getString(R.string.remainingTime).plus(" ")}${
                    formatted.plus(
                        Const.SEPARATOR_SPACE
                    ).plus(context.getString(R.string.second))
                }"

                val spannable = SpannableString(fullMsg)
                val start = fullMsg.indexOf(formatted)
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