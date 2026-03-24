package vn.shb.cam.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardUtils {

    fun showSoftInput(edit: EditText?, context: Context?) {

        if (context == null || edit == null) return

        edit.isFocusable = true
        edit.isFocusableInTouchMode = true
        edit.requestFocus()

        val imm = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edit, 0)
    }

    fun hideSoftInput(activity: Activity?) {
        activity?.findViewById<View>(android.R.id.content)?.let {
            val imm =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun hideSoftInput(edit: EditText) {
        try {
            val imm =
                (edit.context).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}