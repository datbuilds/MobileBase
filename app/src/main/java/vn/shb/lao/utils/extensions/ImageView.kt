package vn.shb.lao.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpFrameCacheStrategy
import com.bumptech.glide.integration.webp.decoder.WebpFrameLoader
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import vn.shb.lao.R

fun ImageView.loadAvatar(
    url: String?,
    @DrawableRes placeholder: Int = vn.shb.lao.ui.R.drawable.ic_avatar_default,
    @DrawableRes error: Int = vn.shb.lao.ui.R.drawable.ic_avatar_default
) {
    try {
        if (url.isNullOrEmpty())
            this.setImageResource(error)
        else
            Glide.with(this).load(url)
                .placeholder(placeholder)
                .error(error)
                .into(this)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholder: Int = vn.shb.lao.ui.R.color.primary_10,
    @DrawableRes error: Int = vn.shb.lao.ui.R.color.accent_10,
    cornerRadiusDp: Int = 8 // bo góc mặc định = 0
) {
    try {
        val radiusPx = cornerRadiusDp.dp2px // convert dp to px

        val requestOptions = RequestOptions()
            .transform(FitCenter(), RoundedCorners(radiusPx))
            .placeholder(placeholder)
            .error(error)

        if (url.isNullOrEmpty()) {
            this.setImageResource(error)
        } else {
            Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .into(this)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}


fun ImageView.loadImageWebp(
    url: String?,
    @DrawableRes placeholder: Int = vn.shb.lao.ui.R.color.primary_10,
    @DrawableRes error: Int = vn.shb.lao.ui.R.color.accent_10
) {
    try {
        if (url.isNullOrEmpty())
            this.setImageResource(error)
        else
            Glide.with(this).load(url)
                .set(WebpFrameLoader.FRAME_CACHE_STRATEGY, WebpFrameCacheStrategy.AUTO)
                .placeholder(placeholder)
                .error(error)
                .into(this)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadImageOriginal(
    url: String?,
    @DrawableRes placeholder: Int = vn.shb.lao.ui.R.color.primary_10,
    @DrawableRes error: Int = vn.shb.lao.ui.R.color.accent_10,
) {
    try {
        if (url.isNullOrEmpty())
            this.setImageResource(error)
        else {
            Glide.with(this).load(url)
                .placeholder(placeholder)
                .error(error)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .dontTransform()
                .into(this)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadAvatarText(
    url: String = "",
    fallbackName: String = "",
    colorBg: String = "",
    colorStoke: String = "",
    colorText: String = ""
) {
    try {
        val placeholder = createLetterAvatar(
            context = context,
            fullName = fallbackName,
            colorBg = colorBg,
            colorStoke = colorStoke,
            colorText = colorText
        )

        if (url.isEmpty())
            this.setImageDrawable(placeholder)
        else {
            Glide.with(this).load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .dontTransform()
                .into(this)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}


/**
 * Tạo avatar với hình tròn và chữ
 * @param context
 */
/**
 * Tạo avatar hình tròn kèm chữ viết tắt, có viền xám
 */
fun createLetterAvatar(
    context: Context,
    fullName: String,
    colorBg: String,
    colorStoke: String,
    colorText: String
): Drawable {
    val initials = getInitials(fullName)
    val size = 100        // px – có thể chuyển sang dp nếu cần
    val strokeWidth = 4f  // độ dày viền

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    /* --------- NỀN (Fill) --------- */
    val paintFill = Paint().apply {
        color = Color.parseColor(colorBg)       // nền trắng
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintFill)

    /* --------- VIỀN (Stroke) --------- */
    val paintStroke = Paint().apply {
        color = Color.parseColor(colorStoke)          // màu xám cho viền
        isAntiAlias = true
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
    }
    // Trừ strokeWidth/2 để viền không bị lấn ra ngoài bitmap
    canvas.drawCircle(
        size / 2f,
        size / 2f,
        size / 2f - strokeWidth / 2f,
        paintStroke
    )

    /* --------- CHỮ (Initials) --------- */
    val paintText = Paint().apply {
        color = Color.parseColor(colorText)  // màu chữ cam
        textSize = size / 2.5f
        isFakeBoldText = true
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    val xPos = size / 2f
    val yPos = size / 2f - (paintText.descent() + paintText.ascent()) / 2f
    canvas.drawText(initials, xPos, yPos, paintText)

    return BitmapDrawable(context.resources, bitmap)
}

/**
 * Lấy 1–2 ký tự đầu của tên để hiển thị làm chữ viết tắt.
 * Ví dụ: "Nguyễn Văn A" -> "NA"
 */
fun getInitials(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "?"
    }
}

