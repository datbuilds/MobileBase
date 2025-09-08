package vn.shb.lao.screens.login.ui.widget

import android.content.Context
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import vn.shb.lao.R
import vn.shb.lao.utils.extensions.clearEditTextColorFilter
import vn.shb.lao.utils.extensions.getGreetingMessage
import vn.shb.lao.utils.extensions.inflate
import vn.shb.lao.utils.extensions.loadAvatarText
import vn.shb.lao.utils.extensions.setErrorAndBackgroundDefault
import vn.shb.lao.utils.extensions.textValue
import vn.shb.lao.utils.extensions.validateLogin
import vn.shb.lao.utils.extensions.visibleWhenTrue
import vn.shb.lao.utils.view.input.TextInputClearError

class LoginUserNameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onUserInputListener: OnUserInputListener? = null

    private val viewAvatarLogged: LinearLayoutCompat
    private val viewInputLogin: LinearLayoutCompat

    private val inputUserNameLayout: TextInputClearError
    private val tvUserName: AppCompatTextView
    private val tvWelcome: AppCompatTextView
    private val ivAvatar: ShapeableImageView
    private val edtUserName: TextInputEditText

    fun onListener(listener: OnUserInputListener) {
        onUserInputListener = listener
    }

    init {
        val itemView = inflate(R.layout.view_login_user_name, true) as ConstraintLayout
        viewAvatarLogged = itemView.findViewById(R.id.viewAvatarLogged)
        viewInputLogin = itemView.findViewById(R.id.viewInputLogin)

        inputUserNameLayout = itemView.findViewById(R.id.inputUserNameLayout)
        tvUserName = itemView.findViewById(R.id.tvUserName)
        tvWelcome = itemView.findViewById(R.id.tvWelcome)
        ivAvatar = itemView.findViewById(R.id.ivAvatar)
        edtUserName = itemView.findViewById(R.id.edUserName)

        tvWelcome.text = getGreetingMessage()

        edtUserName.apply {
            doOnTextChanged { _, _, _, _ ->
                inputUserNameLayout.apply {
                    setErrorAndBackgroundDefault()
                    error = null
                    isErrorEnabled = false
                }
            }

            doAfterTextChanged { text ->
                if (text?.isNotEmpty() == true) {
                    for (span in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
                        text.removeSpan(span)
                    }
                }
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    onUserInputListener?.onLogin()
                    true
                } else {
                    false
                }
            }
        }
    }

    fun setData(userName: String) {
        viewAvatarLogged.visibleWhenTrue(userName.isNotEmpty())
        viewInputLogin.visibleWhenTrue(userName.isEmpty())

        if (userName.isNotEmpty()) {
            tvUserName.text = userName
            ivAvatar.loadAvatarText(
                fallbackName = userName,
                colorBg = "#FC7513",
                colorStoke = "#FC7513",
                colorText = "#FFFFFF"
            )
        }
    }

    fun validateLogin(isValidate: Boolean): Boolean {
        return inputUserNameLayout.validateLogin(isUserName = isValidate)
    }

    fun getUserName() = edtUserName.textValue()

    fun clearEditTextColorFilter() {
        inputUserNameLayout.clearEditTextColorFilter()
    }
}

interface OnUserInputListener {
    fun onLogin()
}