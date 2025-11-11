package vn.shb.lao.screens.transfer.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.ConfirmCodeFragmentBinding
import vn.shb.lao.utils.extensions.CustomCountdownTimer
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.hideSoftKeyboard

class ConfirmCodeBottomSheetDialogFragment(
    private val authSms: String,
    private val actionConfirmCode: (String) -> Unit,
    private val actionDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: ConfirmCodeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfirmCodeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvContentAlert.text = getString(R.string.pleaseEnterConfirmCode, authSms)

            ivCloseDialog.setOnSingleClickListener {
                dismiss()
            }

            edtEnterCode.doAfterTextChanged {
                val enable = !it.toString().isBlank()
                tvConfirm.isEnabled = enable
                tvConfirm.alpha = if (enable) 1f else 0.5f
            }

            edtEnterCode.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus()
                    hideSoftKeyboard()
                    true
                } else {
                    false
                }
            }

            tvConfirm.setOnSingleClickListener {
                actionConfirmCode.invoke(edtEnterCode.text.toString())
                dismiss()
            }

            tvRemainingTime.countdownConfirmCode {
                if (dialog?.isShowing == true) {
                    dismiss()
                }
            }
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        actionDismiss.invoke()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogSlideAnimation
}