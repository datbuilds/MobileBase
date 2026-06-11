package com.mobile.base.screens.transaction

import android.view.View
import com.mobile.base.base.BaseBottomDialogBinding
import com.mobile.base.databinding.DialogSetNicknameBinding
import com.mobile.base.utils.extensions.setLatinAlphanumericFilter
import com.mobile.base.core.utils.extesions.setOnSingleClickListener

class DialogSetNickname(
    private val includeNickname: (String) -> Unit
) : BaseBottomDialogBinding<DialogSetNicknameBinding>(DialogSetNicknameBinding::inflate) {

    override fun initView(view: View) {
//        setEnableButton(binding.edtNickname.text?.trim()?.isNullOrEmpty() != true)

        // Validation logic
//        binding.edtNickname.doAfterTextChanged {
//        val text = it.toString()
//            binding.tvError.text = if (text.isEmpty()) getString(R.string.error_enter_nickname) else getString(R.string.theNameIsInvaid)
//            binding.tvError.isVisible = text.trim().isBlank()
//            setEnableButton(text.trim().isNotEmpty())
//        }

        // Input Filter
        binding.edtNickname.setLatinAlphanumericFilter(MAX_LENGHT_INPUT_NAME)
    }

    private val MAX_LENGHT_INPUT_NAME = 20

    private fun setEnableButton(enable: Boolean) {
        binding.btnConfirm.isEnabled = enable
        binding.btnConfirm.alpha = if (enable) 1f else 0.5f
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnSingleClickListener {
            val nickname = binding.edtNickname.text.toString().trim()
            includeNickname(nickname)
            dismiss()
        }
    }

    override fun initObserve() {
        // Not needed for this dialog
    }

    companion object {
        const val TAG = "DialogSetNickname"
    }
}
