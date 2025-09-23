package vn.shb.lao.screens.login.ui.widget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import vn.shb.lao.R
import vn.shb.lao.screens.login.ui.LoginFragment
import java.util.Calendar

fun LoginFragment.setGreeting(textView: TextView) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 6..11 -> getString(R.string.goodMorning)
        in 12..17 -> getString(R.string.goodAfternoon)
        in 18..23 -> getString(R.string.goodNight)
        else -> getString(R.string.welcome)
    }

    val fullText = "$greeting, $currentUserName"

    val spannable = SpannableString(fullText)
    val start = fullText.indexOf(currentUserName)
    val end = start + currentUserName.length

    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    spannable.setSpan(AbsoluteSizeSpan(14, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val customFont: Typeface? = ResourcesCompat.getFont(context!!, vn.shb.lao.ui.R.font.onest_semi_bold)
    if (customFont != null) {
        spannable.setSpan(customFont, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    textView.text = spannable
}

fun LoginFragment.checkNotificationPermission() {
    when {
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED -> {
            // You can use the API that requires the permission.
        }

        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
            Snackbar.make(
                binding.coordinatorLayout,
                "Sale App cần cấp quyền thông báo trong ứng dụng",
                Snackbar.LENGTH_LONG
            ).setAction("Cài đặt") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }.show()
        }

        else -> {
            // The registered ActivityResultCallback gets the result of this request
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}