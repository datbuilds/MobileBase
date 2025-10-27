package vn.shb.lao.screens.profile

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.dn.choosePhotoHelper.ChoosePhotoHelper
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogChoosePictureBinding
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class DialogChooseProfilePicture() :
    BaseBottomDialogBinding<DialogChoosePictureBinding>(DialogChoosePictureBinding::inflate) {

    private var onAction: ((String) -> Unit)? = null

    companion object {
        const val TAG = "DialogChooseProfilePicture"
    }

    class Build(val action: (String) -> Unit) {
        fun build() = DialogChooseProfilePicture().apply {
            onAction = action
        }
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

    }

    override fun initListener() {
        with(binding) {
            tvTakePhoto.setOnSingleClickListener {
                onAction?.invoke(ChoosePhotoHelper.ActionProfile.CAMERA.toString())
                dismiss()
            }

            tvSelectFromGallery.setOnSingleClickListener {
                onAction?.invoke(ChoosePhotoHelper.ActionProfile.GALLERY.toString())
                dismiss()
            }

            ivClose.setOnSingleClickListener {
                dismiss()
            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
            }
        }
    }
}