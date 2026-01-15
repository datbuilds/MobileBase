package vn.shb.lao.screens.transaction

import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSetNicknameBinding
import vn.shb.lao.utils.extensions.setLatinAlphanumericFilter
import vn.shb.lao.utils.extensions.visible

class DialogSetNickname(
    private val defaultNickname: String = "",
    private val includeNickname: (String) -> Unit
) : BaseBottomDialogBinding<DialogSetNicknameBinding>(DialogSetNicknameBinding::inflate) {

    override fun initView(view: View) {
        setEnableButton(binding.edtNickname.text?.trim()?.isNullOrEmpty() != true)

        // Validation logic
        binding.edtNickname.doAfterTextChanged {
            val text = it.toString()
//            binding.tvError.text = if (text.isEmpty()) getString(R.string.error_enter_nickname) else getString(R.string.theNameIsInvaid)
//            binding.tvError.isVisible = text.trim().isBlank()
            setEnableButton(text.trim().isNotEmpty())
        }

        // Input Filter
        binding.edtNickname.setLatinAlphanumericFilter(50)
    }

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
            if (nickname.isEmpty()) {
//                binding.tvError.text = getString(R.string.error_enter_nickname)
//                binding.tvError.visible()
                return@setOnSingleClickListener
            }
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
