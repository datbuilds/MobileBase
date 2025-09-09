package vn.shb.lao.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import vn.shb.lao.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

val Context.inputWindowManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

//check if network is connected
@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
fun Context.isNetworkAvailable() = run {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var isConnected = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
            isConnected = when {
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    } else
        connectivityManager.activeNetworkInfo?.let {
            isConnected = it.isConnected
        }
    isConnected
}

fun Context.openFacebookMessager(fanpageId: String = "") {
    val LINK_MESSAGE_APP = "fb-messenger://user-thread/%s"
    val LINK_FANPAGE_BROWSED = "https://www.facebook.com/messages/t/%s"

    val uri =
        Uri.parse(
            try {
                applicationContext.packageManager.getPackageInfoCompat(
                    "com.facebook.orca",
                    PackageManager.GET_ACTIVITIES
                ).let {
                    // Installed
                    String.format(LINK_MESSAGE_APP, fanpageId)
                }
            } catch (ex: PackageManager.NameNotFoundException) {
                // not installed it will open your app directly on playstore
                String.format(LINK_FANPAGE_BROWSED, fanpageId)
            }
        )

    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (ex: Exception) {//browser not found
        ex.printStackTrace()
    }
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }


fun openDialer(code: String, context: Context) {
    try {
        val u = Uri.parse("tel:${code.replace("#", Uri.encode("#"))}")
        val intent = Intent(Intent.ACTION_DIAL, u)

        context.startActivity(intent)
    } catch (ex: Exception) {
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Share action
 */
const val CACHE_IMAGE_FILE_NAME = "transaction.png"
private const val CACHE_IMAGE_FOLDER_NAME = "images"

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun Context.cacheBitmap(bitmap: Bitmap, fileName: String, callback: (Boolean) -> Unit) {
    Thread {
        try {
            val cachePath = File(cacheDir, CACHE_IMAGE_FOLDER_NAME).apply {
                deleteRecursively()
                mkdirs()
            }
            val stream = FileOutputStream("$cachePath/${fileName}")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            Handler(Looper.getMainLooper()).post { callback(true) }
        } catch (e: IOException) {
            e.printStackTrace()
            Handler(Looper.getMainLooper()).post { callback(false) }
        }
    }.start()
}

fun Context.shareImage(fileName: String) {
    val imagePath = File(cacheDir, CACHE_IMAGE_FOLDER_NAME)
    val newFile = File(imagePath, fileName)
    val contentUri = FileProvider.getUriForFile(
        Objects.requireNonNull(this), BuildConfig.APPLICATION_ID + ".provider", newFile
    )
    if (contentUri != null) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_STREAM, contentUri)
        }
        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(vn.shb.lao.localization.R.string.message_share_image)
            )
        )
    }
}