package vn.shb.lao.screens.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.utils.extensions.getGreetingMessage
import vn.shb.lao.utils.extensions.loadAvatarText

class HomeWelcomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var tvUserName: AppCompatTextView
    private var tvWelcome: AppCompatTextView
    private var ivNotification: AppCompatImageView
    private var ivAvatar: AppCompatImageView
    private var viewUserName: LinearLayoutCompat

    private var onWelcomeListener: OnWelcomeListener? = null

    fun setListener(onListener: OnWelcomeListener) {
        this.onWelcomeListener = onListener
    }

    init {
        // Inflate layout XML
        val itemLayout = LayoutInflater.from(context)
            .inflate(R.layout.home_welcome_view, this, true) as ConstraintLayout

        tvUserName = itemLayout.findViewById(R.id.tvUserName)
        ivNotification = itemLayout.findViewById(R.id.ivNotification)
        tvWelcome = itemLayout.findViewById(R.id.tvWelcome)
        ivAvatar = itemLayout.findViewById(R.id.ivAvatar)
        viewUserName = itemLayout.findViewById(R.id.viewUserName)

        ivNotification.setOnSingleClickListener {
            onWelcomeListener?.onNotificationClick()
        }
        ivAvatar.setOnSingleClickListener {
            onWelcomeListener?.onAvatarClick()
        }
        viewUserName.setOnSingleClickListener {
            onWelcomeListener?.onAvatarClick()
        }
        tvWelcome.text = getGreetingMessage()
    }

    fun setUserName(userName: String) {
        ivAvatar.loadAvatarText(
            fallbackName = userName,
            colorBg = "#FC7513",
            colorStoke = "#FC7513",
            colorText = "#FFFFFF"
        )
        tvUserName.text = userName
    }
}

interface OnWelcomeListener {
    fun onAvatarClick()
    fun onNotificationClick()
}