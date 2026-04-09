package vn.shb.cam.base.view

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.data.entities.hasDecimal

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


    fun setValidateDataPasteToAmount(
        isAmountField: Boolean,
        currentCurrencyChoose: String,
        MAX_LENGTH_INPUT_AMOUNT: Int
    ) {
        this.isAmountField = isAmountField
        this.currentCurrencyChoose = currentCurrencyChoose
        this.MAX_LENGTH_INPUT_AMOUNT = MAX_LENGTH_INPUT_AMOUNT
    }

    var isAmountField: Boolean = false
    var currentCurrencyChoose: String = ""
    var MAX_LENGTH_INPUT_AMOUNT: Int = -1

    override fun onTextContextMenuItem(id: Int): Boolean {
        // ID của sự kiện Paste trong hệ thống
        if (id == android.R.id.paste && isAmountField && isEnabled && isFocused) {
            // Thực hiện hành động của bạn tại đây
            Log.e("onTextContextMenuItemPaste", "Người dùng vừa Paste text!")
            handlePasteProcess()
            return true

        }
        return super.onTextContextMenuItem(id)
    }

    @SuppressLint("ServiceCast")
    private fun handlePasteProcess() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip

        if (clip != null && clip.itemCount > 0) {
            val rawTextPaste =
                (clip.getItemAt(0).text?.toString() ?: "").replace("[^0-9.]".toRegex(), "")


            val hasSpecialChars = !rawTextPaste.all { it.isDigit() || it == '.' || it == ',' }

            // 2. Kiểm tra số lượng dấu chấm
            val dotCount = rawTextPaste.count { it == '.' }

            var pastedText = if (currentCurrencyChoose == Const.USD) {
                if (dotCount > 1) {
                    // Tìm vị trí dấu chấm cuối cùng
                    val lastDotIndex = rawTextPaste.lastIndexOf('.')

                    // Duyệt qua chuỗi và loại bỏ các dấu chấm không phải là dấu cuối cùng
                    val result = StringBuilder()
                    rawTextPaste.forEachIndexed { index, char ->
                        if (char != '.' || index == lastDotIndex) {
                            result.append(char)
                        }
                    }
                    result.toString()
                } else {
                    // Nếu có 0 hoặc 1 dấu chấm thì giữ nguyên kết quả đã lọc
                    rawTextPaste
                }
            } else {
                rawTextPaste.replace(".", "")
            }

            pastedText = pastedText.replaceFirst("^0+(?=\\d)".toRegex(), "")

            if (currentCurrencyChoose == Const.USD) {
                // 2. Chỉ giữ lại tối đa 2 số sau dấu chấm thập phân
                if (pastedText.contains(".")) {
                    val parts = pastedText.split(".")
                    val intPart = parts[0]
                    val decPart = if (parts.size > 1) parts[1] else ""

                    // Nếu phần thập phân dài hơn 2, ta chỉ lấy 2 ký tự đầu
                    val limitedDecPart = if (decPart.length > 2) decPart.take(2) else decPart

                    pastedText = "$intPart.$limitedDecPart"
                }
            }

            pastedText = if (pastedText.contains(".")) {
                val parts = pastedText.split(".")
                val intPart = parts[0]
                val decPart = if (parts.size > 1) parts[1] else ""

                // Kiểm tra phần nguyên (Giới hạn 12 số)
                val validatedInt = if (intPart.length > 12) {
                    intPart.take(12) // Chỉ lấy 12 số đầu
                } else {
                    intPart
                }

                // Giữ lại phần thập phân (có thể giới hạn 2 số như yêu cầu trước của bạn)
                val limitedDec = decPart.take(2)

                "$validatedInt.$limitedDec"
            } else {
                // 2. Nếu là số nguyên thuần túy
                if (pastedText.length > 12) {
                    pastedText.take(12)
                } else {
                    pastedText
                }
            }

            // 2. Kiểm tra có phải là số không
            val isNumeric = pastedText.toDoubleOrNull() != null

            // 3. Kiểm tra định dạng Decimal (Số thập phân)
            val isDecimal = isNumeric && pastedText.toDoubleOrNull()?.hasDecimal() == true

            // Logic xử lý kết quả
            processValidation(pastedText, isNumeric, isDecimal, hasSpecialChars)
        }
    }

    private fun processValidation(
        text: String,
        isNumeric: Boolean,
        isDecimal: Boolean,
        hasSpecialChars: Boolean
    ) {
        when {
            hasSpecialChars -> {
                // Ví dụ: Thông báo lỗi nếu có ký tự lạ
                Log.e("onTextContextMenuItemPaste", "Văn bản chứa ký tự đặc biệt không hợp lệ")
            }

            isNumeric -> {
                if (isDecimal) {
                    Log.e("onTextContextMenuItemPaste", "là số thập phân :${text}")
                    // Logic nếu là số thập phân (ví dụ: 10.5)
                    setText(text)
                } else {
                    Log.e("onTextContextMenuItemPaste", "là số nguyên :${text}")
                    // Logic nếu là số nguyên (ví dụ: 100)
                    setText(text)
                }
            }

            else -> {
                Log.e("onTextContextMenuItemPaste", "Dữ liệu paste vào không phải là số")
            }
        }
        // Đưa con trỏ xuống cuối văn bản
        setSelection(length())
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