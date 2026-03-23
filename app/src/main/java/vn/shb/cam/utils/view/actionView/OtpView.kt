package vn.shb.cam.utils.view.actionView

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import vn.shb.cam.R

class OtpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val otpLength = 6
    private val textViews = ArrayList<TextView>()
    private var otpCompleteListener: ((String) -> Unit)? = null
    private var otpChangedListener: ((String, Boolean) -> Unit)? = null

    private lateinit var hiddenEditText: EditText
    private lateinit var boxesContainer: LinearLayout

    init {
        setupViews()
    }

    private fun setupViews() {
        // Container for visual boxes
        boxesContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val paramsContainer = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        paramsContainer.gravity = Gravity.CENTER
        addView(boxesContainer, paramsContainer)

        setupOtpBoxes()

        // Hidden EditText for input
        hiddenEditText = EditText(context).apply {
            isCursorVisible = false
            background = null
            setTextColor(Color.TRANSPARENT)
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(otpLength))
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }

        // Add hiddenEditText on top to capture touches
        val paramsHidden = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(hiddenEditText, paramsHidden)

        hiddenEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateOtpBoxes(s?.toString() ?: "")

                val currentOtp = s?.toString() ?: ""
                otpChangedListener?.invoke(currentOtp, currentOtp.length == otpLength)

                if (currentOtp.length == otpLength) {
                    otpCompleteListener?.invoke(currentOtp)
                    // Hide keyboard if needed? Usually better to keep it open or let user decide.
                    // Original code didn't hide it explicitly inside onTextChanged.
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Forward focus to hiddenEditText whenever the view is clicked
        setOnClickListener {
            hiddenEditText.requestFocus()
            // Show keyboard logic could be added here if needed, 
            // but clicking EditText usually handles it.
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
            imm?.showSoftInput(
                hiddenEditText,
                android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    private fun setupOtpBoxes() {
        textViews.clear()
        boxesContainer.removeAllViews()

        val widthBox = resources.displayMetrics.widthPixels / 10

        for (i in 0 until otpLength) {
            val tv = TextView(context)
            val params = LinearLayout.LayoutParams(widthBox, (widthBox * 1.5).toInt())
            params.marginEnd = widthBox / 4
            params.marginStart = widthBox / 4
            tv.layoutParams = params

            tv.setTextColor(ContextCompat.getColor(context, R.color.neutral8))
            tv.typeface = ResourcesCompat.getFont(context, R.font.onest)
            tv.gravity = Gravity.CENTER
            tv.textSize = (widthBox / 4).toFloat()
            tv.setBackgroundResource(R.drawable.bg_edt_otp)

            textViews.add(tv)
            boxesContainer.addView(tv)
        }
    }

    private fun updateOtpBoxes(otp: String) {
        for (i in 0 until otpLength) {
            if (i < otp.length) {
                textViews[i].text = otp[i].toString()
            } else {
                textViews[i].text = ""
            }
        }
    }

    fun setOnOtpChangedListener(listener: (String, Boolean) -> Unit) {
        otpChangedListener = listener
    }

    fun setOtpCompleteListener(listener: (String) -> Unit) {
        otpCompleteListener = listener
    }

    fun setOtp(otp: String) {
        hiddenEditText.setText(otp)
        hiddenEditText.setSelection(otp.length.coerceAtMost(otpLength))
    }

    fun getOtp(): String {
        return hiddenEditText.text.toString()
    }

    fun clearOtp() {
        hiddenEditText.setText("")
        hiddenEditText.requestFocus()
    }
}

