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

            val item = clipData.getItemAt(0)
            val text = normalizeClipboardText(item.coerceToText(context))

            // Nếu clipboard chỉ là image/uri và không coerce được ra text hiển thị được
            if (text.isEmpty() && description != null &&
                (description.hasMimeType("image/*") ||
                    description.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST))
            ) {
                context.toast("Vui lòng chọn upload ảnh")
                return false
            }

            if (!isValidClipboardText(text)) {
                context.toast("Nội dung không hợp lệ")
                return false
            }
        }

        return super.onTextContextMenuItem(id)
    }

    companion object {
        private val invisibleCharactersRegex = Regex("[\\u200B\\u200C\\u200D\\u2060\\uFEFF]")
        private val disallowedControlCharactersRegex = Regex("[\\p{Cntrl}&&[^\\n\\r\\t]]")
        private val contentUriOnlyRegex = Regex("^(content|file)://\\S+$", RegexOption.IGNORE_CASE)

        fun normalizeClipboardText(text: CharSequence?): String {
            return text
                ?.toString()
                ?.replace(invisibleCharactersRegex, "")
                ?.trim()
                .orEmpty()
        }

        fun isValidClipboardText(text: String): Boolean {
            val normalized = normalizeClipboardText(text)
            if (normalized.isBlank()) return false
            if (contentUriOnlyRegex.matches(normalized)) return false
            return !disallowedControlCharactersRegex.containsMatchIn(normalized)
        }
    }
}
