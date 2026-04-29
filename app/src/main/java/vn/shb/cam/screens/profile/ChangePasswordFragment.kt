package vn.shb.cam.screens.profile

import android.content.res.ColorStateList
import android.text.InputFilter
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.base.view.MyTextView
import vn.shb.cam.databinding.FragmentChangePasswordBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.ApiConst.AUTH_111
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.textValue
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.extesions.setOnSingleClickListener
import java.util.Base64

class ChangePasswordFragment :
    BaseFragmentBinding<FragmentChangePasswordBinding>(FragmentChangePasswordBinding::inflate) {

    private val viewModel: ChangePasswordViewModel by inject()
    private val encryptFactory: EncryptManager by inject()

    private var isValidLength = false
    private var isValidCase = false
    private var isValidSpecial = false
    private var isValidUsername = false
    private var isValidNotSame = false

    override fun initView(view: View) {
        // Initial state
        checkConfirmButton()
        highlightSpecialChars(binding.tvRuleSpecial)
        binding.llValidation.visibility = View.GONE
    }

    override fun initListener() {
        with(binding) {
            ivBack.setOnSingleClickListener {
                backPress()
            }

            setupPasswordInput(edtCurrentPassword, tvShowHideCurrent, ivClearCurrent, true)
            setupPasswordInput(edtNewPassword, tvShowHideNew, ivClearNew, true) {
                validatePassword(it)
                checkConditionPassVisibility()
            }
            setupPasswordInput(edtReEnterPassword, tvShowHideReEnter, ivClearReEnter, true)

            edtCurrentPassword.doAfterTextChanged {
                validatePassword(edtNewPassword.textValue())
                checkConfirmButton()
            }
            edtReEnterPassword.doAfterTextChanged {
                checkConfirmButton()
            }

            btnConfirm.setOnSingleClickListener {
                val currentPass = edtCurrentPassword.textValue()
                val newPass = edtNewPassword.textValue()

                val (_, encCurrentPass) = encryptPassword(currentPass)
                val (_, encNewPass) = encryptPassword(newPass)

                viewModel.changePassword(encCurrentPass, encNewPass)
            }
        }
    }

    private val passwordFilter = InputFilter { source, start, end, _, _, _ ->
        for (i in start until end) {
            val char = source[i]
            // Block space and non-ASCII (accents)
            // Allow 33 (!) to 126 (~)
            if (char.code !in 33..126) return@InputFilter ""
        }
        null
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
        applyFilter: Boolean = false,
        onTextChanged: ((String) -> Unit)? = null
    ) {
        // Initial state
        editText.transformationMethod =
            android.text.method.PasswordTransformationMethod.getInstance()
        tvToggle.visibility = View.GONE // Initially hide Show button

        if (applyFilter) {
            val currentFilters = editText.filters
            val newFilters = currentFilters.toMutableList()
            newFilters.add(passwordFilter)
            // Ensure maxLength is respected if set in XML (Filters replace XML maxLength if not handled carefully,
            // but actually separate LengthFilter is added by Android framework.
            // If we replace the array, we might lose it. But here we append.)
            // However, InputFilter.LengthFilter is usually automatically added from XML android:maxLength.
            // Let's preserve existing filters.
            editText.filters = newFilters.toTypedArray()
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            tvToggle.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        tvToggle.setOnSingleClickListener {
            val selectionStart = editText.selectionStart
            val selectionEnd = editText.selectionEnd

            if (editText.transformationMethod is android.text.method.PasswordTransformationMethod) {
                editText.transformationMethod =
                    android.text.method.HideReturnsTransformationMethod.getInstance()
                tvToggle.text = getString(R.string.hide)
            } else {
                editText.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
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
                            lifecycleScope.launch {
                                homeViewModel.stateLoading(false)
                            }
                            viewModel.resetState()
                            viewOldPasswordError(false)
                            safeNavigate(AppDestination.ChangePasswordSuccess)
                        }

                        is ChangePasswordState.Error -> {
                            lifecycleScope.launch {
                                homeViewModel.stateLoading(false)
                            }
                            if (state.reason.errorCode == ApiConst.AUTH_010) {
                                viewOldPasswordError()
                                return@collect
                            }
                            if (state.reason.errorCode == AUTH_111) {
                                binding.ivI.setImageResource(R.drawable.ic_close_circle)
                                return@collect
                            }
                            viewOldPasswordError(false)
                            handleErrorHome(state.reason)
                            viewModel.resetState()
                        }

                        is ChangePasswordState.Loading -> {
                            lifecycleScope.launch {
                                homeViewModel.stateLoading(true)
                            }
                        }

                        else -> {
                            lifecycleScope.launch {
                                homeViewModel.stateLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun viewOldPasswordError(isView: Boolean = true) {
        binding.tvErrorCurrentPassword.isVisible = isView
        enableButtonConfirm(false)
    }

    private fun validatePassword(password: String) {
        val currentPass = binding.edtCurrentPassword.textValue()

        // 0. Not same as old password
        val isSame = password.isNotEmpty() && password == currentPass
        binding.tvErrorSamePassword.isVisible = isSame
        isValidNotSame = !isSame

        // 1. Length 8-20
        isValidLength = password.length in 8..20
        updateValidationStatus(binding.tvRuleLength, isValidLength)

        // 2. Lowercase and Uppercase
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        isValidCase = hasLower && hasUpper
        updateValidationStatus(binding.tvRuleCase, isValidCase)

        // 3. Numeric and Special
        val hasDigit = password.any { it.isDigit() }
        val specialChars = ".~!@#\$%^&*()_+={}|:;<>,/?-\""
        val hasSpecial = password.any { specialChars.contains(it) }
        isValidSpecial = hasDigit && hasSpecial
        updateValidationStatus(binding.tvRuleSpecial, isValidSpecial)

        // 4. No username or real name
        val user = getCurrentUser()
        val username =
            user?.customerId?.replace("\\s+".toRegex(), " ")?.trim() ?: getCurrentUser()?.userLogin
            ?: ""
        val realName = user?.username?.replace("\\s+".toRegex(), "")?.trim() ?: ""

        // Simple check: password should not contain username or real name (ignoring case)
        val containsUsername =
            if (username.isNotEmpty()) password.contains(username, ignoreCase = true) else false
        val containsRealName =
            if (realName.isNotEmpty()) password.contains(realName, ignoreCase = true) else false

        isValidUsername = !containsUsername && !containsRealName
        updateValidationStatus(binding.tvRuleUsername, isValidUsername)

        checkConfirmButton()
    }

    private fun updateValidationStatus(textView: MyTextView, isValid: Boolean) {
        val iconColor = if (isValid) R.color.green_500 else R.color.red_600
        val icon = if (isValid) R.drawable.ic_success else R.drawable.ic_error

        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        TextViewCompat.setCompoundDrawableTintList(
            textView, ColorStateList.valueOf(getColor(iconColor))
        )
    }

    private fun highlightSpecialChars(textView: MyTextView) {
        val text = textView.text.toString()
        val specialChars = ".~!@#$%^*()_+=|{};<>,/?-"
        val start = text.indexOf(specialChars.take(5)) // Match start of special chars
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

    private fun checkConditionPassVisibility() {
        with(binding) {
            val isNewPassInput = edtNewPassword.textValue().isNotEmpty()
            if (isNewPassInput && llValidation.visibility != View.VISIBLE) {
                llValidation.visibility = View.VISIBLE
            } else if (!isNewPassInput && llValidation.isVisible) {
                llValidation.visibility = View.GONE
            }
        }
    }

    private fun checkConfirmButton() {
        with(binding) {
            val currentPass = edtCurrentPassword.text.toString()
            val newPass = edtNewPassword.text.toString()
            val reEnterPass = edtReEnterPassword.text.toString()

            val isMatch = newPass == reEnterPass
            val isAllValid =
                isValidLength && isValidCase && isValidSpecial && isValidUsername && isValidNotSame
            val isCurrentNotEmpty = currentPass.isNotEmpty()

            // Show match error if mismatch and re-enter is not empty
            tvErrorReEnterPassword.visibility =
                if (!isMatch && reEnterPass.isNotEmpty()) View.VISIBLE else View.GONE

            val enable = isAllValid && isMatch && isCurrentNotEmpty && newPass.isNotEmpty()

            enableButtonConfirm(isEnable = enable)
        }
    }

    private fun enableButtonConfirm(isEnable: Boolean) {
        binding.btnConfirm.isEnabled = isEnable
        binding.btnConfirm.alpha = if (isEnable) 1f else 0.5f
    }
}
