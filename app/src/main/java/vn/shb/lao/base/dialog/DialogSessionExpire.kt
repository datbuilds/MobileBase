package vn.shb.lao.base.dialog

import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSessionExpireBinding

class DialogSessionExpire(private val build: Build) :
    BaseBottomDialogBinding<DialogSessionExpireBinding>(DialogSessionExpireBinding::inflate) {

    override fun initView(view: View) {}

    override fun initListener() {
        with(binding) {
            ivClose.setOnSingleClickListener {
                dismissAllowingStateLoss()
            }
            btnLogin.setOnSingleClickListener {
                build.onLogOut()
                dismissAllowingStateLoss()
            }
        }
    }

    override fun initObserve() {

    }

    class Build(val onLogOut: () -> Unit) {
        fun build() = DialogSessionExpire(this)
    }

    companion object {
        const val TAG = "DialogSessionExpire"
    }
}