package com.mobile.base.choosephotohelper.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

private const val MAX_IMAGE_EDGE_PX = 640
private const val JPEG_QUALITY = 85

/**
 * @author aminography
 */

/**
 * @param bitmap
 * @param degrees
 *
 * @return
 */
fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * @param bitmap
 * @param horizontal
 * @param vertical
 *
 * @return
 */
fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * @param bitmap
 * @param absolutePath
 *
 * @return modified bitmap
 */
suspend fun modifyOrientationSuspending(bitmap: Bitmap, absolutePath: String): Bitmap =
    withContext(Dispatchers.IO) {
        modifyOrientation(bitmap, absolutePath)
    }

/**
 * @param bitmap
 * @param absolutePath
 *
 * @return modified bitmap
 */
@Throws(IOException::class)
fun modifyOrientation(bitmap: Bitmap, absolutePath: String): Bitmap {
    val exif = ExifInterface(absolutePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(
            bitmap,
            90f
        )

        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(
            bitmap,
            180f
        )

        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(
            bitmap,
            270f
        )

        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
            bitmap,
            horizontal = true,
            vertical = false
        )

        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
            bitmap,
            horizontal = false,
            vertical = true
        )

        else -> bitmap
    }
}

/**
 * @param absolutePath
 *
 * @return
 */
@Throws(IOException::class)
fun modifyOrientationAndResize(absolutePath: String): ByteArray? {
    if (absolutePath.isBlank()) return null

    val sourceFile = File(absolutePath)
    if (!sourceFile.exists() || !sourceFile.isFile) return null

    var workingBitmap = BitmapFactory.decodeFile(absolutePath) ?: return null

    try {
        val resizedBitmap = resizeBitmap(workingBitmap)
        if (resizedBitmap !== workingBitmap) {
            workingBitmap.recycle()
            workingBitmap = resizedBitmap
        }

        try {
            val orientedBitmap = modifyOrientation(workingBitmap, absolutePath)
            if (orientedBitmap !== workingBitmap) {
                workingBitmap.recycle()
                workingBitmap = orientedBitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ByteArrayOutputStream().use { outputStream ->
            val isCompressed = workingBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                JPEG_QUALITY,
                outputStream
            )
            if (!isCompressed) return null

            outputStream.toByteArray().takeIf { it.isNotEmpty() }
        }
    } finally {
        if (!workingBitmap.isRecycled) {
            workingBitmap.recycle()
        }
    }
}

private fun resizeBitmap(bitmap: Bitmap): Bitmap {
    val sourceWidth = bitmap.width
    val sourceHeight = bitmap.height

    if (sourceWidth <= 0 || sourceHeight <= 0) return bitmap

    val (targetWidth, targetHeight) = if (sourceHeight > sourceWidth) {
        val scaledWidth = (MAX_IMAGE_EDGE_PX * (sourceWidth.toDouble() / sourceHeight.toDouble()))
            .toInt()
            .coerceAtLeast(1)
        scaledWidth to MAX_IMAGE_EDGE_PX
    } else {
        val scaledHeight = (MAX_IMAGE_EDGE_PX * (sourceHeight.toDouble() / sourceWidth.toDouble()))
            .toInt()
            .coerceAtLeast(1)
        MAX_IMAGE_EDGE_PX to scaledHeight
    }

    if (sourceWidth == targetWidth && sourceHeight == targetHeight) {
        return bitmap
    }

    return bitmap.scale(targetWidth, targetHeight)
}
