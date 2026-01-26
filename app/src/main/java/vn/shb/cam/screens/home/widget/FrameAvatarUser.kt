package vn.shb.cam.screens.home.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.TextViewCompat
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getInitials
import vn.shb.cam.databinding.FlAvatarUserBinding
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.loadAvatar
import vn.shb.cam.utils.extensions.loadImage
import vn.shb.cam.utils.extensions.visible

class FrameAvatarUser @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var onClickDetail: OnClickDetail? = null
    private val binding: FlAvatarUserBinding =
        FlAvatarUserBinding.inflate(LayoutInflater.from(context), this, true)

    fun setListener(onListener: OnClickDetail) {
        this.onClickDetail = onListener
    }

    init {
        binding.root.setOnSingleClickListener {
            onClickDetail?.onAvatarClick()
        }
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            binding.tvImageUser,
            7, 18, 1, TypedValue.COMPLEX_UNIT_SP
        )
    }

    fun setUserName(urlAvatar : String? = "", userName: String = "") {
        with(binding) {
            if (!urlAvatar.isNullOrEmpty()){
                ivImageUser.loadAvatar(urlAvatar)
                ivImageUser.visible()
                tvImageUser.gone()
            } else {
                tvImageUser.text = userName.getInitials()
                ivImageUser.gone()
                tvImageUser.visible()
            }
        }
    }
}


interface OnClickDetail {
    fun onAvatarClick()
}