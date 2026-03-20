package vn.shb.cam.screens.paste2pay.widget

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import vn.shb.cam.utils.extensions.toast

class SafePasteEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    init {
        imeOptions = EditorInfo.IME_ACTION_DONE
    }

    override fun onTextContextMenuItem(id: Int): Boolean {

        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {

            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = clipboard.primaryClip ?: return false
            val description = clipboard.primaryClipDescription

            // nếu clipboard là image / uri
            if (description != null &&
                (description.hasMimeType("image/*") ||
                    description.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST))
            ) {
                context.toast("Vui lòng chọn upload ảnh")
                return false
            }

            val item = clipData.getItemAt(0)
            val text = item.coerceToText(context).toString()

            // kiểm tra ký tự hợp lệ
            if (!isValidClipboardText(text)) {
                context.toast("Nội dung không hợp lệ")
                return false
            }
        }

        return super.onTextContextMenuItem(id)
    }

    companion object {
        private val regex = Regex("^[a-zA-Z0-9.,\\s]*$")

        fun isValidClipboardText(text: String): Boolean = regex.matches(text)
    }
}
