package vn.shb.lao.screens.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getInitials
import vn.shb.lao.databinding.FlAvatarUserBinding
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.loadImage
import vn.shb.lao.utils.extensions.visible

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
    }

    fun setUserName(urlAvatar : String = "", userName: String = "") {
        with(binding) {
            if (urlAvatar.isNotEmpty()){
                ivImageUser.loadImage(urlAvatar)
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