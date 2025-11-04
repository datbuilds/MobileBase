package vn.shb.lao.screens.transfer

import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import vn.shb.data.entities.DecimalDigitsInputFilter
import vn.shb.data.entities.getBalanceFormatted
import vn.shb.lao.R
import vn.shb.lao.base.view.MyEditText
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.ItemTransferTypeBinding
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import java.text.Normalizer

fun MoneyTransferFragment.viewOptionTransfer(tv: MyTextView, typeView: String) {
    val isChoose = currentTypeTransfer == typeView
    tv.setTextColor(getColor(if (isChoose) R.color.neutral1 else R.color.primary100))
    tv.setBackgroundResource(if (isChoose) R.drawable.bg_transfer_choose else R.drawable.bg_transfer_normal)
}

fun removeVietnameseAccents(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

fun MyEditText.setInputEditText(onlyNumber: Boolean, isTypeSigned: Boolean = false) {
    when {
        !onlyNumber -> {
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }

        isTypeSigned -> {
            filters = arrayOf(DecimalDigitsInputFilter(2))
            keyListener = DigitsKeyListener.getInstance("0123456789.")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        else -> {
            inputType =
                android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            keyListener = DigitsKeyListener.getInstance("0123456789")
        }
    }
}

fun MyEditText.enableInput(enable: Boolean) {
    isFocusable = enable
    isFocusableInTouchMode = enable
    isClickable = !enable
    isLongClickable = enable
}

fun MyEditText.setupDecimalInput(maxDecimal: Int = 2) {

    addTextChangedListener(object : TextWatcher {
        private var current = ""
        private var editing = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (editing) return
            val text = s?.toString() ?: return
            if (text == current) return

            editing = true

            try {
                // Lưu lại vị trí con trỏ hiện tại
                val cursorStart = selectionStart
                val cursorEnd = selectionEnd

                // Không format khi user đang nhập dấu "." ở cuối
                if (text.endsWith(".") || text == "." || text.isEmpty()) {
                    current = text
                } else {
                    val clean = text.replace(",", "")
                    val formatted = clean.getBalanceFormatted()

                    // Tính độ chênh lệch độ dài giữa chuỗi cũ và mới (để bù trừ vị trí con trỏ)
                    val diff = formatted.length - text.length

                    current = formatted
                    setText(formatted)

                    // Tính toán lại vị trí con trỏ sao cho không bị nhảy ra cuối
                    val newPos = (cursorStart + diff).coerceIn(0, formatted.length)
                    setSelection(newPos)
                }
            } catch (_: Exception) {
            }

            editing = false
        }
    })
}

fun MoneyTransferFragment.resetRemarks() {
    remarks = getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
        .plus(getString(R.string.transferCAP))
    binding.iclRemarks.edtValue.setText(remarks)
}

fun ItemTransferTypeBinding.bindViewError(text: String? = null) {
    val isError = text != null
    tvError.isVisible = isError
//    edtValue.alpha = if (isError) 0.6f else 1f
    if (!isError) return
    tvError.text = text
}

fun ItemTransferTypeBinding.bindColor(idColor : Int){
    val color = ContextCompat.getColor(root.context, idColor)
    edtValue.setTextColor(color)
    tvCurrentCode.setTextColor(color)
}

fun Fragment.finishTyping(
    myEditText: MyEditText,
    isIntra: Boolean,
    callBack: () -> Unit
) {
    with(myEditText) {
        setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                hideSoftKeyboard()
                callBack.invoke()
                true
            } else {
                false
            }
        }

        setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && isIntra) {
                callBack.invoke()
            }
        }
    }

}