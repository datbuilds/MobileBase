package vn.shb.lao.screens.profile

import android.view.View
import androidx.core.widget.doAfterTextChanged
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment :
    BaseFragmentBinding<FragmentChangePasswordBinding>(FragmentChangePasswordBinding::inflate) {

    private var isValidLength = false
    private var isValidCase = false
    private var isValidSpecial = false
    private var isValidUsername = false

    override fun initView(view: View) {
        // Initial state
        checkConfirmButton()
    }

    override fun initListener() {
        with(binding) {
            ivBack.setOnSingleClickListener {
                backPress()
            }

            setupPasswordInput(edtCurrentPassword, tvShowHideCurrent, ivClearCurrent)
            setupPasswordInput(edtNewPassword, tvShowHideNew, ivClearNew) {
                validatePassword(it)
            }
            setupPasswordInput(edtReEnterPassword, tvShowHideReEnter, ivClearReEnter)

            edtCurrentPassword.doAfterTextChanged { checkConfirmButton() }
            edtReEnterPassword.doAfterTextChanged { checkConfirmButton() }

            btnConfirm.setOnSingleClickListener {
                // Handle confirm click (API call logic would go here)
                backPress()
            }
        }
    }

    private fun setupPasswordInput(
        editText: android.widget.EditText,
        tvToggle: MyTextView,
        ivClear: androidx.appcompat.widget.AppCompatImageView,
        onTextChanged: ((String) -> Unit)? = null
    ) {
        // Initial state
        editText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        
        tvToggle.setOnSingleClickListener {
            val selectionStart = editText.selectionStart
            val selectionEnd = editText.selectionEnd
            
            if (editText.transformationMethod is android.text.method.PasswordTransformationMethod) {
                editText.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                tvToggle.text = getString(R.string.hide)
            } else {
                editText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                tvToggle.text = getString(R.string.show)
            }
            // Restore cursor position
            editText.setSelection(selectionStart, selectionEnd)
        }

        ivClear.setOnSingleClickListener {
            editText.text = null
        }

        editText.doAfterTextChanged { editable ->
            val text = editable.toString()
            ivClear.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
            onTextChanged?.invoke(text)
        }
    }

    override fun initObserve() {
        // No specific observation requirements yet
    }

    private fun validatePassword(password: String) {
        // 1. Length 6-50
        isValidLength = password.length in 6..50
        updateValidationStatus(binding.tvRuleLength, isValidLength)

        // 2. Lowercase and Uppercase
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        isValidCase = hasLower && hasUpper
        updateValidationStatus(binding.tvRuleCase, isValidCase)

        // 3. Numeric and Special
        val hasDigit = password.any { it.isDigit() }
        val specialChars = ".~!@#$%^*()_+=|{};<>,/?"
        val hasSpecial = password.any { specialChars.contains(it) }
        isValidSpecial = hasDigit && hasSpecial
        updateValidationStatus(binding.tvRuleSpecial, isValidSpecial)

        // 4. No username or real name
        val user = getCurrentUser()
        val username = user?.username ?: ""
        val realName = user?.username ?: ""

        // Simple check: password should not contain username or real name (ignoring case)
        val containsUsername = if (username.isNotEmpty()) password.contains(username, ignoreCase = true) else false
        val containsRealName = if (realName.isNotEmpty()) password.contains(realName, ignoreCase = true) else false

        isValidUsername = !containsUsername && !containsRealName
        updateValidationStatus(binding.tvRuleUsername, isValidUsername)

        checkConfirmButton()
    }

    private fun updateValidationStatus(textView: MyTextView, isValid: Boolean) {
        val color = if (isValid) R.color.green_500 else R.color.red_600 // Assuming colors exist
        val icon = if (isValid) R.drawable.ic_success else R.drawable.ic_error

        textView.setTextColor(getColor(color))
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        // Tint compound drawable if needed, but setCompoundDrawablesWithIntrinsicBounds usually doesn't tint automatically unless we use setCompoundDrawableTintList
        // The xml used app:drawableTint, programmatically we might need to set tint.
        // Let's rely on setCompoundDrawablesWithIntrinsicBounds and ensure icons are colored or handle tint.
        // Given XML has app:drawableTint, let's try to set tint programmatically.
        androidx.core.widget.TextViewCompat.setCompoundDrawableTintList(
            textView,
            android.content.res.ColorStateList.valueOf(getColor(color))
        )
    }

    private fun checkConfirmButton() {
        with(binding) {
            val currentPass = edtCurrentPassword.text.toString()
            val newPass = edtNewPassword.text.toString()
            val reEnterPass = edtReEnterPassword.text.toString()

            val isMatch = newPass == reEnterPass && newPass.isNotEmpty()
            val isAllValid = isValidLength && isValidCase && isValidSpecial && isValidUsername
            val isCurrentNotEmpty = currentPass.isNotEmpty()

            val enable = isAllValid && isMatch && isCurrentNotEmpty

            btnConfirm.isEnabled = enable
            // Style update if needed for disabled state, usually handled by selector/button style
        }
    }
}
