package vn.shb.cam.utils.view.actionView

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import vn.shb.cam.R

class OtpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val otpLength = 6
    private val editTexts = ArrayList<EditText>()
    private var otpCompleteListener: ((String) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setupOtpFields()
    }

    private fun setupOtpFields() {
        for (i in 0 until otpLength) {
            val edt = EditText(context)
            val params = LayoutParams(100, 150)
            params.marginEnd = 16
            params.marginStart = 16
            edt.layoutParams = params

            edt.setTextColor(ContextCompat.getColor(context, R.color.neutral8))
            edt.typeface = ResourcesCompat.getFont(context, R.font.onest)
            edt.gravity = Gravity.CENTER
            edt.textSize = 32f
            edt.inputType = InputType.TYPE_CLASS_NUMBER
            edt.filters = arrayOf(InputFilter.LengthFilter(1))
            edt.setBackgroundResource(R.drawable.bg_edt_otp)

            edt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 👉 paste full OTP
                    if (s != null && s.length > 1) {
                        setOtp(s.toString())
                        return
                    }

                    if (s?.length == 1) {
                        if (i < otpLength - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                        checkOtpComplete()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edt.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    edt.text.isEmpty() &&
                    i > 0
                ) {
                    editTexts[i - 1].requestFocus()
                }
                false
            }

            editTexts.add(edt)
            addView(edt)
        }
    }

    fun setOtpCompleteListener(listener: (String) -> Unit) {
        otpCompleteListener = listener
    }

    private fun checkOtpComplete() {
        val otp = getOtp()
        if (otp.length == otpLength) {
            otpCompleteListener?.invoke(otp)
        }
    }

    fun setOtp(otp: String) {
        clearOtp()
        for (i in otp.indices) {
            if (i < otpLength) {
                editTexts[i].setText(otp[i].toString())
            }
        }
        checkOtpComplete()
    }

    fun getOtp(): String {
        return editTexts.joinToString("") { it.text.toString() }
    }

    fun clearOtp() {
        editTexts.forEach { it.setText("") }
        editTexts[0].requestFocus()
    }
}

