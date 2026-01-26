package vn.shb.cam.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import vn.shb.cam.R

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), text, duration).show()

fun Fragment.showToastCenter(text: CharSequence) {
    val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Fragment.showToastWithAction(
    rootView: ViewGroup,
    textAction: CharSequence,
    callBack: () -> Unit
) {
    Snackbar.make(rootView, "Đã tải xong dữ liệu", Snackbar.LENGTH_LONG)
        .setAction(textAction) {
            callBack()
        }.show()
}


fun Fragment.customSnackBar(
    rootView: ViewGroup,
    icon: Int,
    text: CharSequence
) {
    val snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_LONG)

    val inflater = layoutInflater
    val layout = inflater.inflate(R.layout.custom_toast, rootView)
    val ivStatus = layout.findViewById<AppCompatImageView>(R.id.ivToastStatus)
    val tvMessage = layout.findViewById<AppCompatTextView>(R.id.tvToastMessage)
//    val ivClose = layout.findViewById<AppCompatImageView>(R.id.ivToastClose)

    ivStatus.setImageResource(icon)
    tvMessage.text = text

    // Lấy ra view của Snackbar
    val snackbarView = snackbar.view

    // Đặt nền màu trong suốt
    snackbarView.setBackgroundColor(Color.TRANSPARENT)

    // Loại bỏ hiệu ứng đổ màu cho Snackbar
    snackbarView.setPadding(0, 0, 0, 0)

    // Đặt độ cao cho Snackbar
    val layoutParams = snackbarView.layoutParams as ViewGroup.LayoutParams
    layoutParams.height = resources.getDimensionPixelSize(R.dimen.margin_10dp)
    snackbarView.layoutParams = layoutParams

    // Thiết lập nội dung tùy chỉnh cho Snackbar
//    snackbar.view.removeAllViews()
//    snackbar.view.addView(layout)

    snackbar.show()
}

@SuppressLint("RestrictedApi")
class CustomToastShowOnTop(
    context: Context,
    view: View,
    icon: Int,
    message: String,
    background: Int = R.drawable.bg_custom_success,
    duration: Long
) {

    private val snackBarLayout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
    private val container = snackBarLayout?.findViewById<LinearLayoutCompat>(R.id.containerToast)
    private val ivStatus = snackBarLayout?.findViewById<AppCompatImageView>(R.id.ivToastStatus)
    private val tvMessage = snackBarLayout?.findViewById<AppCompatTextView>(R.id.tvToastMessage)
    private val ivClose = snackBarLayout?.findViewById<AppCompatImageView>(R.id.ivToastClose)

    private val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        .setDuration(duration.toInt())
    var layout = snackBar.view as Snackbar.SnackbarLayout

    init {
        ivStatus?.setImageResource(icon)
        tvMessage?.text = message
        container?.setBackgroundResource(background)

        val params = WindowManager.LayoutParams()
        params.gravity = Gravity.TOP or Gravity.CENTER
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.Transparent))
        layout.layoutParams = params
        layout.removeAllViews()
        layout.addView(snackBarLayout)

        ivClose?.setOnClickListener {
            snackBar.dismiss()
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ snackBar.dismiss() }, duration)
    }

    fun show() {
        snackBar.show()
    }

    fun dismiss() {
        if (snackBar.isShown) snackBar.dismiss()
    }
}