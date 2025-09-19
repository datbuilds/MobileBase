package vn.shb.lao.base.dialog

import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogResponseStatusBinding
import vn.shb.lao.utils.extensions.fromHtml

class DialogWarningAccessibilityPermission(private val build: Build) :
    BaseBottomDialogBinding<DialogResponseStatusBinding>
        (DialogResponseStatusBinding::inflate) {

    companion object {
        const val TAG = "DialogWarningAccessibilityPermission"
    }

    override fun initView(view: View) {
        with(binding) {
            tvTitle.text = requireContext().getString(vn.shb.lao.localization.R.string.title_noti)
            tvContentError.fromHtml(getString(vn.shb.lao.localization.R.string.warning_accessibility_permission))
        }
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            finish()
        }

        binding.btnClose.setOnSingleClickListener {
            finish()
        }
    }

    private fun finish() {
        build.onFinish()
        dismiss()
    }

    override fun initObserve() {}

    class Build(val onFinish: () -> Unit) {
        fun build() = DialogWarningAccessibilityPermission(this)
    }
}