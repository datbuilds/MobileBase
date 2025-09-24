package vn.shb.lao.utils.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import vn.shb.lao.R

fun TextInputLayout.hideStartIcon() {
    this.startIconDrawable = null
}

fun TextInputLayout.showStartIcon(context: Context, icon: Int) {
    this.startIconDrawable = ContextCompat.getDrawable(context, icon)
}

fun TextInputLayout.disableInput() {
    this.isErrorEnabled = false
    editText?.isEnabled = false
    this.error = null
}

fun TextInputLayout.enableInput() {
    this.isErrorEnabled = false
    editText?.isEnabled = true
    this.error = null
}

fun TextInputLayout.clearEditTextColorFilter() {
    editText?.setOnFocusChangeListener { _, _ ->
        editText?.background?.clearColorFilter()
    }
    editText?.background?.clearColorFilter()
    editText?.clearFocus()
}

fun TextInputLayout.validateReason(): Boolean {
    val inputText = this.editText?.text?.trim().toString()
    return when {
        inputText.isEmpty() -> {
            setErrorAndBackground("Quý khách vui lòng nhập lý do từ chối")
            false
        }

        else -> {
            this.error = ""
            true
        }
    }
}

fun TextInputLayout.validateLogin(isUserName: Boolean = false): Boolean {
    val inputText = this.editText?.text?.toString()
    return when {
        inputText.isNullOrEmpty() -> {
            setErrorAndBackground(if (isUserName) "Vui lòng nhập tên tài khoản" else "Vui lòng nhập mật khẩu")
            false
        }

        else -> {
            this.error = ""
            true
        }
    }
}

fun TextInputLayout.validateInputForgotPassword(number: Int): Boolean {
    val inputText = this.editText?.text?.toString()
    return when {
        inputText.isNullOrEmpty() -> {
            setErrorAndBackground(
                when (number) {
                    1 -> "Quý khách vui lòng nhập mã số đăng ký kinh doanh"
                    2 -> "Quý khách vui lòng nhập Email"
                    3 -> "Quý khách vui lòng nhập số điên thoại"
                    else -> "Quý khách vui lòng chọn vai trò"
                }
            )
            false
        }

        else -> {
            this.error = ""
            true
        }
    }
}

fun TextInputLayout.isSamePass(retryNewPass: String, newPass: String): Boolean {
    val isSamePass = retryNewPass.contentEquals(newPass)

    if (!isSamePass) {
        setErrorAndBackground("Mật khẩu không trùng nhau")
    } else {
        setErrorAndBackgroundDefault()
    }

    return isSamePass
}

fun TextInputLayout.isValidPassword(): Boolean {
    val inputText = this.editText?.text.toString()

    when {
        inputText.isEmpty() -> {
            setErrorAndBackground("Vui lòng nhập mật khẩu")
            return false
        }

        inputText.contains(" ") -> {
            setErrorAndBackground("Mật khẩu không được chứa khoảng trắng")
            return false
        }

        inputText.length < 8 -> {
            setErrorAndBackground("Mật khẩu cần dài ít nhất 8 ký tự")
            return false
        }

        !inputText.any { it.isLowerCase() } || !inputText.any { it.isUpperCase() } -> {
            setErrorAndBackground("Mật khẩu chứa ít nhất 1 ký tự viết thường, 1 ký tự viết hoa")
            return false
        }

        !inputText.any { it in "~!@#$%^&*()_+?" } -> {
            setErrorAndBackground("Mật khẩu chứa ít nhất 1 ký tự số, 1 ký tự đặc biệt")
            return false
        }

        else -> {
            this.error = null
            return true
        }
    }
}

fun TextInputLayout.addPasswordValidator(
    context: Context,
    tvLengthValid: TextView,
    tvHasLowerOrUpperCase: TextView,
    tvIncludesNumeric: TextView,
    tvIncludesSpecial: TextView,
) {
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val password = s.toString()

            val lengthValid = password.length in 8..20
            val hasLowerCase = password.any { it.isLowerCase() }
            val hasUpperCase = password.any { it.isUpperCase() }
            val includesNumeric = password.matches(".*\\d.*".toRegex())
            val hasSpecialChar = password.any { it in "~!@#$%^&*()_+?" }

            isErrorEnabled = !(lengthValid && hasLowerCase && hasUpperCase && hasSpecialChar)
            error = "Mật khẩu chưa hợp lệ"

            if (password.isEmpty()) {
                tvLengthValid.setDrawableIconByState(context, IconState.NORMAL)
                tvHasLowerOrUpperCase.setDrawableIconByState(context, IconState.NORMAL)
                tvIncludesNumeric.setDrawableIconByState(context, IconState.NORMAL)
                tvIncludesSpecial.setDrawableIconByState(context, IconState.NORMAL)
            } else {
                tvLengthValid.setDrawableIconByState(
                    context,
                    if (lengthValid) IconState.SUCCESS else IconState.FAILURE
                )
                tvHasLowerOrUpperCase.setDrawableIconByState(
                    context,
                    if (hasLowerCase && hasUpperCase) IconState.SUCCESS else IconState.FAILURE
                )
                tvIncludesNumeric.setDrawableIconByState(
                    context,
                    if (includesNumeric) IconState.SUCCESS else IconState.FAILURE
                )
                tvIncludesSpecial.setDrawableIconByState(
                    context,
                    if (hasSpecialChar) IconState.SUCCESS else IconState.FAILURE
                )
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

fun TextInputLayout.setErrorAndBackground(errorMessage: String) {
    error = errorMessage
    editText?.setBackgroundResource(R.drawable.bg_edt_error)
}

fun TextInputLayout.setErrorAndBackgroundDefault() {
    error = null
    editText?.setBackgroundResource(R.drawable.selector_edt)
}

private fun TextInputLayout.clearErrorAndBackground() {
    error = null
    editText?.setBackgroundResource(0)
}

fun TextInputLayout.clearText() {
    editText?.text?.clear()
}

fun TextInputEditText.textNotEmpty(): Boolean {
    return this.text?.trim().toString().isNotEmpty()
}

fun TextInputEditText.textValue(): String {
    return this.text?.trim().toString()
}