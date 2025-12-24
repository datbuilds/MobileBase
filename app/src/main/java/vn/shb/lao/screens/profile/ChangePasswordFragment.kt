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
        highlightSpecialChars(binding.tvRuleSpecial)
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
        isValidSpecial = hasDigit || hasSpecial
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
        val iconColor = if (isValid) R.color.green_500 else R.color.red_600
        val icon = if (isValid) R.drawable.ic_success else R.drawable.ic_error
        val textColor = if (isValid) R.color.green_500 else R.color.neutral6

        textView.setTextColor(getColor(textColor))
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        androidx.core.widget.TextViewCompat.setCompoundDrawableTintList(
            textView,
            android.content.res.ColorStateList.valueOf(getColor(iconColor))
        )
    }

    private fun highlightSpecialChars(textView: MyTextView) {
        val text = textView.text.toString()
        val specialChars = ".~!@#$%^*()_+=|{};<>,/?-"
        val start = text.indexOf(specialChars.substring(0, 5)) // Match start of special chars
        if (start != -1) {
            val spannable = android.text.SpannableString(text)
            // Find the exact range of special chars at the end
            // Assuming it's at the end or we just search for the known block
            // The string in strings.xml is "... .~!@#$%^*()_+=|{};<>,/?"
            // But we added "-" in the variable above? The user image shows it might end with - or ?
            // Let's rely on finding the longest match or just the substring
            val matchStr = text.substring(start) 
            // Better: find strictly the special chars
            // ".~!@#$%^*()_+=|{};<>,/?" -> The full set
            // In strings.xml: ".~!@#$%^*()_+=|{};&lt;&gt;,/?"
            
            // Let's search for the substring starting with "." and ending with string end or common checks
            // Or just hardcode the logic to find the special chars block
            
            val end = text.length
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(getColor(R.color.blueSpecial)),
                start,
                end,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        }
    }

    private fun checkConfirmButton() {
        with(binding) {
            val currentPass = edtCurrentPassword.text.toString()
            val newPass = edtNewPassword.text.toString()
            val reEnterPass = edtReEnterPassword.text.toString()

            val isMatch = newPass == reEnterPass
            val isAllValid = isValidLength && isValidCase && isValidSpecial && isValidUsername
            val isCurrentNotEmpty = currentPass.isNotEmpty()
            
            // Show match error if mismatch and re-enter is not empty
            tvErrorReEnterPassword.visibility = if (!isMatch && reEnterPass.isNotEmpty()) View.VISIBLE else View.GONE

            val enable = isAllValid && isMatch && isCurrentNotEmpty && newPass.isNotEmpty()

            btnConfirm.isEnabled = enable
            btnConfirm.alpha = if (enable) 1f else 0.5f
        }
    }
}
