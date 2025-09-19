package vn.shb.lao.utils.view.actionView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.utils.extensions.inflate
import vn.shb.lao.utils.extensions.invisible
import vn.shb.lao.utils.extensions.visible

class ToolbarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var tvTitle: AppCompatTextView
    private var ivBack: AppCompatImageView

    private var listener: OnToolbarListener? = null

    fun setListener(listener: OnToolbarListener) {
        this.listener = listener
    }

    init {
        val itemLayout = inflate(R.layout.view_toolbar, true) as LinearLayoutCompat

        ivBack = itemLayout.findViewById(R.id.ivBack)
        tvTitle = itemLayout.findViewById(R.id.tvTitle)

        ivBack.setOnSingleClickListener {
            listener?.onPreviousClick()
        }
    }

    fun setTitle(title: String, isShow: Boolean = true) {
        tvTitle.text = title
        if (isShow) {
            ivBack.visible()
        } else {
            ivBack.invisible()
        }
    }
}

interface OnToolbarListener {
    fun onPreviousClick()
//    fun onNextClick()
}