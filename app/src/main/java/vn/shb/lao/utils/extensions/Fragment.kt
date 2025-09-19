package vn.shb.lao.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import vn.shb.lao.utils.view.dialog.ProgressDialogUtil
import java.io.Serializable

fun Fragment.inflatView(@LayoutRes layoutId: Int) = layoutInflater.inflate(layoutId, null)

fun Fragment.showProgressDialog() {
    ProgressDialogUtil.show(requireContext())
}

@SuppressLint("SuspiciousIndentation")
fun Fragment.hideProgressDialog() {
    if (activity?.isDestroyed != true && activity?.isFinishing == true) return
    ProgressDialogUtil.dismiss()
}

fun Fragment.showSoftKeyboard(view: View) {
    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.hideSoftKeyboard(flag: Int = 0) {
    view?.let { view ->
        view.context?.inputWindowManager?.run {
            if (isActive) {
                hideSoftInputFromWindow(view.windowToken, flag)
            }
        }
    }
}

fun Fragment.setStatusBarAndNavigationBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.requireActivity().window.statusBarColor = requireContext().getColor(color)
        this.requireActivity().window.navigationBarColor = requireContext().getColor(color)
    }
}

fun Fragment.setupActionBarWithNavController(toolbar: MaterialToolbar) {

//     Định nghĩa margin (sử dụng dp)
//    val marginInDp = 24
//    val marginInPx = TypedValue.applyDimension(
//        TypedValue.COMPLEX_UNIT_DIP, marginInDp.toFloat(),
//        resources.displayMetrics
//    ).toInt()
//    // Lấy LayoutParams hiện tại
//    val layoutParams = toolbar.layoutParams as ViewGroup.MarginLayoutParams
//    layoutParams.setMargins(0, marginInPx, 0, 0)
//    // Gán lại LayoutParams vào Toolbar
//    toolbar.layoutParams = layoutParams

    val activity = requireActivity() as AppCompatActivity
    val navController = findNavController()

    activity.setSupportActionBar(toolbar)
    activity.supportActionBar?.setDisplayShowTitleEnabled(false)
    NavigationUI.setupActionBarWithNavController(activity, navController)
}

fun AppCompatActivity.setupActionBarWithNavController(toolbar: MaterialToolbar) {
    this.setSupportActionBar(toolbar)
    this.supportActionBar?.setDisplayShowTitleEnabled(false)
}

fun BottomSheetDialogFragment.showKeyboard(inputLayout: TextInputLayout) {
    val inputMethodManager =
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputLayout.requestFocus()
    dialog?.window?.decorView?.postDelayed({
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }, 120)
}

inline fun <reified T : Any> Fragment.extra(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key)
    if (value is T) value else default
}

inline fun <reified T : Any> Fragment.extraNotNull(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key)
    requireNotNull(if (value is T) value else default) { key }
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}


fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

@SuppressLint("NewApi", "DiscouragedPrivateApi")
fun Fragment.getAllBiometrics(): String {
    val fingerprintManager = requireContext()
        .getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager ?: return ""

    val result = StringBuilder()

    try {
        val method = FingerprintManager::class.java
            .getDeclaredMethod("getEnrolledFingerprints")
            .apply { isAccessible = true }

        val enrolledList = method.invoke(fingerprintManager) as? List<*> ?: return ""

        val fingerprintClass = Class.forName("android.hardware.fingerprint.Fingerprint")

        for (finger in enrolledList) {
            if (finger != null) {
                val fingerprintInfo = when {
                    Build.VERSION.SDK_INT > Build.VERSION_CODES.P -> {
                        try {
                            val writeToParcel = fingerprintClass.getDeclaredMethod(
                                "writeToParcel", Parcel::class.java, Int::class.javaPrimitiveType
                            ).apply { isAccessible = true }

                            val parcel = Parcel.obtain()
                            writeToParcel.invoke(finger, parcel, 0)
                            parcel.setDataPosition(0)
                            val resultId = parcel.readLong()
                            parcel.recycle()
                            resultId.toString()
                        } catch (e: Exception) {
                            "unknown"
                        }
                    }

                    else -> {
                        try {
                            val getFingerId = fingerprintClass
                                .getDeclaredMethod("getFingerId")
                                .apply { isAccessible = true }
                            getFingerId.invoke(finger)?.toString() ?: "unknown"
                        } catch (e: Exception) {
                            "unknown"
                        }
                    }
                }
                result.append("$fingerprintInfo*")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result.toString()
}

fun Fragment.dismissAllDialogsInParent() {
    val fragments = this.childFragmentManager.fragments
    for (fragment in fragments) {
        if (fragment is DialogFragment) {
            fragment.dismissAllowingStateLoss()
        }
    }
}
