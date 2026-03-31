package vn.shb.cam.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import vn.shb.cam.R

class MyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var clearIconDrawable: Drawable? = null

    private var isAllowShowButtonClearText: Boolean = true

    init {
        // --- LOGIC CŨ CỦA BẠN (Giữ nguyên) ---
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

        // --- LOGIC THÊM DẤU X (Đã fix lỗi Vector) ---
        setupClearIcon()
    }

    private fun setupClearIcon() {
        // 1. Dùng AppCompatResources để load Vector an toàn và gọi mutate()
        clearIconDrawable =
            AppCompatResources.getDrawable(context, R.drawable.ic_clear_text)?.mutate()

        // 2. Ép kích thước an toàn cho Vector
        clearIconDrawable?.let { drawable ->
            var iconWidth = drawable.intrinsicWidth
            var iconHeight = drawable.intrinsicHeight

            // Nếu Vector bị lỗi trả về kích thước <= 0, ép cứng nó về 24dp
            if (iconWidth <= 0 || iconHeight <= 0) {
                val dp24 = (24 * resources.displayMetrics.density).toInt()
                iconWidth = dp24
                iconHeight = dp24
            }

            drawable.setBounds(0, 0, iconWidth, iconHeight)
        }

        // 3. Lắng nghe thay đổi text
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateClearIconVisibility()
            }
        })

        updateClearIconVisibility()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        updateClearIconVisibility()
    }

    private fun updateClearIconVisibility() {
        val isVisible =
            !text.isNullOrEmpty() && isEnabled && isAllowShowButtonClearText && isFocused

        if (isEnabled) {
            clearIconDrawable?.setTint(context.getColor(R.color.neutral8))
        }
        val endDrawable = if (isVisible) clearIconDrawable else null

        val currentDrawables = compoundDrawablesRelative
//        setCompoundDrawablesWithIntrinsicBounds(null,null,endDrawable,null)

        setCompoundDrawablesRelative(
            currentDrawables[0], // Start
            currentDrawables[1], // Top
            endDrawable,         // End (Dấu X)
            currentDrawables[3]  // Bottom
        )
    }


    fun setStateShowButtonClear(state: Boolean) {
        isAllowShowButtonClearText = state
        updateClearIconVisibility()
    }

    // Tách logic kiểm tra tọa độ ra một hàm riêng cho sạch code
    private fun isTouchOnClearIcon(event: MotionEvent): Boolean {
        // Chỉ xử lý khi icon X đang hiển thị
        if (clearIconDrawable == null || text.isNullOrEmpty() || !isEnabled || !isAllowShowButtonClearText || !isFocused) {
            return false
        }

        val isRtl = layoutDirection == LAYOUT_DIRECTION_RTL
        val touchSlop = (10 * resources.displayMetrics.density).toInt()

        return if (isRtl) {
            val clearButtonEnd = paddingStart + clearIconDrawable!!.bounds.width() + touchSlop
            event.x <= clearButtonEnd
        } else {
            val clearButtonStart =
                width - paddingEnd - clearIconDrawable!!.bounds.width() - touchSlop
            event.x >= clearButtonStart
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Nếu người dùng đang chạm trúng khu vực dấu X
        if (isTouchOnClearIcon(event)) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // CỰC KỲ QUAN TRỌNG: Trả về true để "nuốt" sự kiện DOWN.
                    // Ngăn không cho EditText nhận biết thao tác chạm, giúp CHẶN POPUP PASTE.
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    // Khi nhấc ngón tay lên thì tiến hành xóa text
                    text?.clear()
                    performClick()
                    return true
                }
            }
        }

        // Nếu chạm ở ngoài dấu X (chạm vào vùng chữ), cứ để EditText xử lý bình thường (sẽ hiện popup như hệ thống)
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    // --- CÁC HÀM CŨ CỦA BẠN ---

    fun setTypeFaceFont(typeFaceNew: Typeface) {
        this.typeface = typeFaceNew
    }

    fun setTextAndDisableFocus(value: String?) {
        if (value.isNullOrEmpty()) return

        this.setText(value)
        isEnabled = false
        setTextColor(context.getColor(R.color.neutral6))
        clearFocus()

        // Gọi lại update để ẩn icon X vì isEnabled đã bằng false
        updateClearIconVisibility()
    }

    fun setDefaultEdittext() {
        this.setText("")
        isEnabled = true
        setTextColor(context.getColor(R.color.neutral10))
        clearFocus()

        // Gọi lại update để đảm bảo trạng thái icon đồng bộ
        updateClearIconVisibility()
    }
}