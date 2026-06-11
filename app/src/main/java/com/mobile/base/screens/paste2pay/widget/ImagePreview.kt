package com.mobile.base.screens.paste2pay.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import java.io.File
import com.mobile.base.R
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.loadImage
import com.mobile.base.utils.extensions.visible
import com.mobile.base.utils.extensions.inflate
import com.mobile.base.core.utils.extesions.setOnSingleClickListener

class ImagePreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val ivPreview: AppCompatImageView
    private val tvFileName: AppCompatTextView
    private val ivDelete: AppCompatImageView

    private var onDeleteClick: (() -> Unit)? = null

    init {
        val itemLayout = inflate(R.layout.view_image_preview, true) as LinearLayoutCompat
        ivPreview = itemLayout.findViewById(R.id.ivPreview)
        tvFileName = itemLayout.findViewById(R.id.tvFileName)
        ivDelete = itemLayout.findViewById(R.id.ivDelete)

        ivDelete.setOnSingleClickListener {
            onDeleteClick?.invoke()
        }
    }

    fun setPreview(imagePath: String?, fileName: String = imagePath.toFileName()) {
        setFileName(fileName)
        if (imagePath.isNullOrBlank()) {
            ivPreview.setImageResource(R.drawable.ic_gallery_holder)
            return
        }
        ivPreview.loadImage(
            url = imagePath,
            placeholder = R.drawable.ic_gallery_holder,
            error = R.drawable.ic_gallery_holder,
            cornerRadiusDp = 10
        )
    }

    fun setFileName(fileName: String?) {
        tvFileName.text = fileName.orEmpty()
    }

    fun setOnDeleteClickListener(listener: (() -> Unit)?) {
        onDeleteClick = listener
    }

    fun showDeleteButton(isShow: Boolean) {
        if (isShow) ivDelete.visible() else ivDelete.gone()
    }

    fun clear() {
        ivPreview.setImageResource(0)
        tvFileName.text = ""
    }

    private fun String?.toFileName(): String {
        if (this.isNullOrBlank()) return ""
        return runCatching { File(this).name }.getOrDefault(this)
    }
}
