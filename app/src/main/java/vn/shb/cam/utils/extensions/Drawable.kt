package vn.shb.cam.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

// Cache cho resourceId
private val drawableIdCache = mutableMapOf<String, Int>()

fun loadDrawableIdByName(context: Context, imageName: String): Int {
    return try {
        drawableIdCache.getOrPut(imageName) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

// Extension function để gọi nhanh
fun Context.safeGetDrawable(name: String): Drawable? {
    return try {
        // Tìm trong tất cả drawable folders (drawable, drawable-nodpi, drawable-hdpi, etc.)
        val resId = resources.getIdentifier(name, "drawable", packageName)
        if (resId == 0) {
            // Debug log để kiểm tra
            println("Drawable Debug: Không tìm thấy icon '$name' trong resources")
            return null
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // API >= 21 load bình thường
            ContextCompat.getDrawable(this, resId)
        } else {
            // API < 21 cần dùng AppCompat cho vector
            val drawable = ContextCompat.getDrawable(this, resId)
            if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
                // Vector -> tạo bản tương thích
                AppCompatResources.getDrawable(this, resId)
            } else {
                drawable // PNG hoặc bitmap bình thường
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Drawable Debug: Lỗi khi load icon '$name': ${e.message}")
        null
    }
}

// Method helper để load icon với fallback
fun Context.loadIconWithFallback(iconName: String, fallbackIconName: String = "ic_khac"): Drawable? {
    // Thử load icon chính
    val mainIcon = safeGetDrawable(iconName)
    if (mainIcon != null) {
        return mainIcon
    }
    
    // Nếu không có thì load fallback icon
    val fallbackIcon = safeGetDrawable(fallbackIconName)
    if (fallbackIcon != null) {
        println("Drawable Debug: Sử dụng fallback icon '$fallbackIconName' cho '$iconName'")
        return fallbackIcon
    }
    
    println("Drawable Debug: Không tìm thấy cả icon chính '$iconName' và fallback '$fallbackIconName'")
    return null
}

