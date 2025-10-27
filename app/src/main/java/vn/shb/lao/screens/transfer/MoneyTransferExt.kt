package vn.shb.lao.screens.transfer

import android.text.Editable
import android.text.TextWatcher
import vn.shb.data.entities.getBalance
import vn.shb.lao.R
import vn.shb.lao.base.view.MyEditText
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.utils.extensions.common.Const

fun MoneyTransferFragment.viewOptionTransfer(tv: MyTextView, typeView: String) {
    val isChoose = currentTypeTransfer == typeView
    tv.setTextColor(getColor(if (isChoose) R.color.neutral1 else R.color.primary100))
    tv.setBackgroundResource(if (isChoose) R.drawable.bg_transfer_choose else R.drawable.bg_transfer_normal)
}

fun MyEditText.setInputEditText(onlyNumber: Boolean) {
    inputType = if (onlyNumber) {
        android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
    } else {
        android.text.InputType.TYPE_CLASS_TEXT
    }
}

fun MyEditText.enableInput(enable: Boolean) {
    isFocusable = enable
    isFocusableInTouchMode = enable
    isClickable = !enable
    isLongClickable = enable
}

fun MyEditText.onTypingAmount(stringFormat: (String) -> Unit) {

    addTextChangedListener(object : TextWatcher {
        private var current = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                removeTextChangedListener(this)

                try {
                    val cleanString = s.toString().replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = parsed.getBalance()
                        current = formatted
                        setText(formatted)
                        setSelection(formatted.length)
                    } else {
                        current = ""
                    }
                    stringFormat.invoke(current)
                } catch (_: Exception) {
                }

                addTextChangedListener(this)
            }
        }
    })

}

fun MoneyTransferFragment.resetRemarks() {
    remarks = getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
        .plus(getString(R.string.transferCAP))
    binding.iclRemarks.edtValue.setText(remarks)
}