package vn.shb.cam.screens.transfer

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import vn.shb.cam.R
import vn.shb.cam.base.view.MyEditText
import vn.shb.cam.base.view.MyTextView
import vn.shb.cam.databinding.ItemTransferTypeBinding
import vn.shb.cam.databinding.LayoutChooseCurrencyPopupBinding
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.hideSoftKeyboard
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.DecimalDigitsInputFilter
import vn.shb.data.entities.getBalanceFormatted
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

fun String.cleanVietnameseText(): String {
    // 1️⃣ Chuẩn hóa Unicode để tách dấu
    var normalized = Normalizer.normalize(this, Normalizer.Form.NFD)

    // 2️⃣ Xóa dấu tổ hợp (ắ → a)
    normalized = normalized.replace(
        "\\p{InCombiningDiacriticalMarks}+".toRegex(),
        ""
    )

    // 3️⃣ Chuyển Đ/đ → D/d
    normalized = normalized.replace("Đ", "D").replace("đ", "d")

    // 4️⃣ CHỈ giữ: a-z, A-Z, 0-9, khoảng trắng
    // ❌ Loại bỏ Lào, Thái, Khmer, Trung, Nhật, Hàn, emoji...
    normalized = normalized.replace(
        "[^a-zA-Z0-9 ]+".toRegex(),
        ""
    )

    return normalized
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

fun MyEditText.setupDecimalInput(currencyProvider: (() -> String)? = null) {

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
            val currency = currencyProvider?.invoke() ?: Const.KHR
            val isUSD = currency == Const.USD

            if (text.isNotEmpty()) {

                if (!isUSD && text.startsWith("0")) {
                    setText("")
                    current = ""
                    editing = false
                    return
                }

                if (isUSD && text.startsWith("0") && text.length > 1 && !text.startsWith("0.")) {
                    val clean0 = text.replaceFirst("^0+(?!$)".toRegex(), "")
                    if (clean0 != text) {
                        setText(clean0)
                        setSelection(clean0.length)
                        current = clean0
                        editing = false
                        return
                    }
                }

                if (text.startsWith(".")) {
                    val prefix = if (isUSD) "0." else ""
                    setText(prefix)
                    if (prefix.isNotEmpty()) setSelection(2)
                    current = prefix
                    editing = false
                    return
                }

                val numeric = text.replace(",", "").toDoubleOrNull()
                if (numeric != null) {
                    if (isUSD) {
                        if (numeric == 0.0 && text.replace(",", "") == "0.00") {
                            setText("")
                            current = ""
                            editing = false
                            return
                        }
                    } else {
                        if (numeric < 1) {
                            setText("")
                            current = ""
                            editing = false
                            return
                        }
                    }
                }
            }

            try {
                // Lưu lại vị trí con trỏ hiện tại
                val cursorStart = selectionStart

                val isZeroDecimal = isUSD && (text == "0" || (text.startsWith("0.0") && text.replace(",", "") == "0.0"))

                // Không format khi user đang nhập dấu "." ở cuối
                if (text.endsWith(".") || text == "." || text.isEmpty() || isZeroDecimal) {
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

fun ItemTransferTypeBinding.bindViewError(text: String? = null) {
    val isError = text != null
    tvError.isVisible = isError
    if (!isError) return
    tvError.text = text
}

fun ItemTransferTypeBinding.bindColor(idColor: Int) {
    val color = ContextCompat.getColor(root.context, idColor)
    edtValue.setTextColor(color)
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

fun MoneyTransferFragment.popupChooseCurrency(
    currentCurrency: String,
    anchor: View,
    choose: (String) -> Unit
) {
    val binding = LayoutChooseCurrencyPopupBinding.inflate(LayoutInflater.from(anchor.context))

    val popupWindow = PopupWindow(
        binding.root,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )

    // style
    popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    popupWindow.isOutsideTouchable = true
    popupWindow.elevation = 8f

    binding.apply {

        tvKHR.apply {
            setTextColor(getColor(if (currentCurrency == Const.KHR) R.color.neutral10 else R.color.neutral7))
            setOnSingleClickListener {
                choose.invoke(Const.KHR)
                popupWindow.dismiss()
            }
        }
        tvUSD.apply {
            setTextColor(getColor(if (currentCurrency == Const.USD) R.color.neutral10 else R.color.neutral7))
            setOnSingleClickListener {
                choose.invoke(Const.USD)
                popupWindow.dismiss()
            }
        }
    }

    val marginRight = (77 * anchor.context.resources.displayMetrics.density).toInt()
    popupWindow.showAsDropDown(anchor, -marginRight, 20, Gravity.END)
}
