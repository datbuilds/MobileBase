package vn.shb.cam.base

import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.containsBoldTag
import vn.shb.cam.R
import vn.shb.cam.databinding.DialogBaseErrorStatusBinding
import vn.shb.cam.utils.extensions.setOnMaterialButtonClick

class BaseErrorDialog(private val build: Build) :
    BaseBottomDialogBinding<DialogBaseErrorStatusBinding>(
        DialogBaseErrorStatusBinding::inflate
    ) {

    class Build(
        val title: String,
        val message: String,
        val tvAction: String,
        val icon: Int = R.drawable.ic_warning,
        val onClose: (() -> Unit?)? = null,
        val onDismiss: (() -> Unit?)? = null, // Thêm callback onDismiss
        val allowDismiss: Boolean = true // Thêm flag để kiểm soát việc dismiss
    ) {
        fun build() = BaseErrorDialog(this)
    }

    companion object {
        const val TAG = "DialogBaseErrorStatus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog_Rounded)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isCancelable = false
            }
            behavior.isDraggable = false
        }
    }

    override fun initView(view: View) {
        showBottomSheetSmooth(binding.bottomSheet)
        binding.tvTitle.text = build.title
        handleContentError(build.message)
        binding.btnClose.text = build.tvAction

        // Set dialog không thể dismiss nếu allowDismiss = false
        isCancelable = build.allowDismiss
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            if (build.allowDismiss) {
                build.onDismiss?.invoke() // Gọi onDismiss khi user click close
                dismissAllowingStateLoss()
            }
        }

        binding.btnClose.setOnMaterialButtonClick {
            build.onClose?.invoke()
            dismissAllowingStateLoss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Gọi onDismiss callback khi dialog bị dismiss (có thể do back press, touch outside, etc.)
        build.onDismiss?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        // Chỉ cho phép cancel nếu allowDismiss = true
        if (build.allowDismiss) {
            super.onCancel(dialog)
        }
    }

    private fun handleContentError(message: String) {
        binding.tvContent.text =
            if (message.containsBoldTag()) {
                Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
            } else {
                message
            }
    }

    override fun initObserve() {}
}
