package vn.shb.cam.base.dialog

import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogDeviceRootBinding

class DialogWarningDeviceRoot(private val build: Build) :
    BaseBottomDialogBinding<DialogDeviceRootBinding>(DialogDeviceRootBinding::inflate) {
    override fun initView(view: View) {

    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }
        binding.btnDeviceRootClose.setOnSingleClickListener {
            build.actionDeviceRoot.invoke()
            dismiss()
        }
    }

    override fun initObserve() {}

    class Build(val actionDeviceRoot: () -> Unit) {
        fun build() = DialogWarningDeviceRoot(this)
    }

    companion object {
        const val TAG = "DialogDeviceRoot"
    }
}