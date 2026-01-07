package vn.shb.lao.screens.transaction

import android.view.View
import androidx.core.widget.doAfterTextChanged
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSetNicknameBinding
import vn.shb.lao.utils.extensions.setLatinAlphanumericFilter
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.utils.extensions.visible
import vn.shb.lao.utils.extensions.gone

class DialogSetNickname(
    private val includeNickname : (String) -> Unit
) : BaseBottomDialogBinding<DialogSetNicknameBinding>(DialogSetNicknameBinding::inflate) {

    override fun initView(view: View) {
        // Validation logic
        binding.edtNickname.doAfterTextChanged {
            val text = it.toString().trim()
            if (text.isNotEmpty()) {
                binding.tvError.gone()
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.setBackgroundResource(R.drawable.bg_button_enable)
            } else {
                binding.btnConfirm.isEnabled = false
                binding.btnConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            }
        }
        
        // Initial state disable
        binding.btnConfirm.isEnabled = false
        
        // Input Filter
        binding.edtNickname.setLatinAlphanumericFilter(50)
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnSingleClickListener {
            val nickname = binding.edtNickname.text.toString().trim()
            if (nickname.isEmpty()) {
                binding.tvError.text = getString(R.string.error_enter_nickname)
                binding.tvError.visible()
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
