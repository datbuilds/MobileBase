package vn.shb.cam.base.view

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import vn.shb.cam.R

class MyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyTextView,
            0, 0
        ).apply {
            typeface = try {
                when (getInt(R.styleable.MyTextView_customFontWeight, 0)) {
                    1 -> FontManager.medium
                    2 -> FontManager.bold
                    3 -> FontManager.semi_bold
                    else -> FontManager.regular
                }
            } finally {
                recycle()
            }
        }
        isFocusable = true
        isFocusableInTouchMode = true
        inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    fun setTypeFaceFont(typeFaceNew:Typeface) {
        this.typeface = typeFaceNew
    }

    fun setTextAndDisableFocus(value:String?){
        if (value.isNullOrEmpty())return

        this.setText(value)
        isEnabled = false
        setTextColor(context.getColor(R.color.neutral6))
        clearFocus()
    }

    fun setDefaultEdittext(){
        this.setText("")
        isEnabled = true
        setTextColor(context.getColor(R.color.neutral10))
        clearFocus()
    }
}