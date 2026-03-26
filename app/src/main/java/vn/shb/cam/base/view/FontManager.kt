package vn.shb.cam.base.view

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

object FontManager {
    var regular: Typeface? = null
    var medium: Typeface? = null
    var bold: Typeface? = null
    var semi_bold: Typeface? = null

    fun init(context: Context, fontName: String? = null) {

        if (fontName.isNullOrBlank()) {
            // Nếu là tiếng CAM → dùng 1 font duy nhất
            val camFont = ResourcesCompat.getFont(
                context,
                context.resources.getIdentifier("siemreap_regular", "font", context.packageName)
            )
            regular = camFont
            medium = camFont
            bold = camFont
            semi_bold = camFont
            return
        }

        regular = ResourcesCompat.getFont(
            context,
            context.resources.getIdentifier("${fontName}_regular", "font", context.packageName)
        )
        medium = ResourcesCompat.getFont(
            context,
            context.resources.getIdentifier("${fontName}_medium", "font", context.packageName)
        )
        bold = ResourcesCompat.getFont(
            context,
            context.resources.getIdentifier("${fontName}_bold", "font", context.packageName)
        )
        semi_bold = ResourcesCompat.getFont(
            context,
            context.resources.getIdentifier("${fontName}_semi_bold", "font", context.packageName)
        )

    }
}
