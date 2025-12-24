package vn.shb.lao.screens.profile

import android.view.View
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.FragmentChangePasswordBinding
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.textValue
import java.util.Base64

class ChangePasswordFragment :
    BaseFragmentBinding<FragmentChangePasswordBinding>(FragmentChangePasswordBinding::inflate) {

    private val viewModel: ChangePasswordViewModel by inject()
    private val encryptFactory: vn.shb.core.core.security.encrypt.EncryptManager by inject()

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
                val currentPass = edtCurrentPassword.textValue()
                val newPass = edtNewPassword.textValue()

                val (_, encCurrentPass) = encryptPassword(currentPass)
                val (_, encNewPass) = encryptPassword(newPass)

                viewModel.changePassword(encCurrentPass, encNewPass)
            }
        }
    }

    private fun encryptPassword(password: String): Pair<String, String> {
        val pswEncrypt = encryptFactory.encryptRSA(plainText = password)
        val encPsw = Base64.getEncoder().encodeToString(pswEncrypt)
        return Pair(password, encPsw)
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
        launchRepeatOnLifecycle {
            launch {
                viewModel.state.collect { state ->
                    when (state) {
                        is ChangePasswordState.Success -> {
                            viewModel.resetState()
                            safeNavigate(
                                R.id.changePasswordFragment,
                                R.id.action_changePasswordFragment_to_changePasswordSuccessFragment
                            )
                        }
                        is ChangePasswordState.Error -> {
                            handleErrorHome(state.reason)
                            viewModel.resetState()
                        }
                        else -> {
                        }
                    }
                }
            }
        }
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
