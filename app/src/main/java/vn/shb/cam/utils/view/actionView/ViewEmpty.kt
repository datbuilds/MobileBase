package vn.shb.cam.utils.view.actionView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.inflate

class ViewEmpty @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayoutCompat(context, attrs, defStyle) {

    private val tvEmpty: AppCompatTextView

    init {
        // Inflate layout XML
        val itemLayout = inflate(R.layout.view_empty, true) as LinearLayoutCompat
        tvEmpty = itemLayout.findViewById(R.id.tvEmpty)
    }

    fun setTextEmpty(text: String) {
        tvEmpty.text = text
    }
}
