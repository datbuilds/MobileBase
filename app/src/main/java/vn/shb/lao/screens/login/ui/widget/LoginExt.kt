package vn.shb.lao.screens.login.ui.widget

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.snackbar.Snackbar
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.databinding.LayoutLanguagePopupBinding
import vn.shb.lao.screens.login.ui.LoginFragment
import vn.shb.lao.utils.extensions.getTextWelcomeUser

fun LoginFragment.setGreeting(textView: TextView) {

    val greeting = context!!.getTextWelcomeUser()

    val fullText = "$greeting, $currentUserName"

    val spannable = SpannableString(fullText)
    val start = fullText.indexOf(currentUserName)
    val end = start + currentUserName.length

    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    spannable.setSpan(AbsoluteSizeSpan(14, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val customFont: Typeface? =
        ResourcesCompat.getFont(context!!, R.font.onest_semi_bold)
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

fun LoginFragment.showLanguagePopup(anchor: View) {
    val binding = LayoutLanguagePopupBinding.inflate(LayoutInflater.from(anchor.context))

    val popupWindow = PopupWindow(
        binding.root,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true // focusable, click outside sẽ tự đóng
    )

    // style
    popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    popupWindow.isOutsideTouchable = true
    popupWindow.elevation = 8f

    binding.apply {
        iclLanguage1.apply {
            ivLogo.setImageResource(R.drawable.ic_logo_uk)
            tvNameLanguage.text = getString(R.string.englishLabel)
            root.setOnSingleClickListener {
                updateLanguage("en")
                popupWindow.dismiss()
            }
        }

        iclLanguage2.apply {
            ivLogo.setImageResource(R.drawable.ic_logo_vn)
            tvNameLanguage.text = getString(R.string.vietnameseLabel)
            root.setOnSingleClickListener {
                updateLanguage("vi")
                popupWindow.dismiss()
            }
        }

        iclLanguage3.apply {
            ivLogo.setImageResource(R.drawable.ic_logo_lao)
            tvNameLanguage.text = getString(R.string.laoLabel)
            root.setOnSingleClickListener {
                updateLanguage("lo")
                popupWindow.dismiss()
            }
        }
    }

    val marginRight = (130 * anchor.context.resources.displayMetrics.density).toInt()
    popupWindow.showAsDropDown(anchor, -marginRight, -60, Gravity.END)
}

fun Context.getResourceLocale(type: String, res: (String, Int) -> Unit) {
    when (type) {
        "en" -> {
            res(getString(R.string.englishLabel), R.drawable.ic_logo_uk)
        }

        "lo" -> {
            res(getString(R.string.laoLabel), R.drawable.ic_logo_lao)
        }

        "vi" -> {
            res(getString(R.string.vietnameseLabel), R.drawable.ic_logo_vn)
        }

        else -> {
            res(getString(R.string.englishLabel), R.drawable.ic_logo_uk)
        }
    }
}
