package vn.shb.lao.utils.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import vn.shb.lao.R
import vn.shb.core.utils.logE

class ToolTipDialog(
    context: Context,
    parentActivity: Activity,
    themeStyleRes: Int = R.style.TooltipDialogTheme_Dark
) : Dialog(context, themeStyleRes) {
    private var arrowWidth = ScreenUtils.getPixels(context, 15f)
    private var contentView: RelativeLayout
    private var container: ViewGroup
    private var upArrow: ImageView
    private var downArrow: ImageView
    private var contentText: TextView
    private var buttonClose: MaterialButton
    private var peekThroughViews = ArrayList<View>()
    private var windowHeight: Int
    private var windowWidth: Int
    private var statusBarHeight: Int

    private var clickOutSideToDismiss: Boolean = true
    private var positionX = 0
    private var positionY = 0

    private var colorFilter: Int? = null

    private var positionGravity = Position.AUTO

    init {
        setContentView(R.layout.tootip_dialog)
        contentView = findViewById(R.id.tooltip_dialog_content_view)
        container = findViewById(R.id.container)
        upArrow = findViewById(R.id.tooltip_top_arrow)
        downArrow = findViewById(R.id.bottom_arrow)
        contentText = findViewById(R.id.tooltip_content)
        buttonClose = findViewById(R.id.button_close)

        // Find the size of the application window
        val usableView = parentActivity.window.findViewById<View>(Window.ID_ANDROID_CONTENT)
        windowHeight = usableView.height
        windowWidth = usableView.width
        val rectgle = Rect()
        val window = window
        window!!.decorView.getWindowVisibleDisplayFrame(rectgle)
        val StatusBarHeight = rectgle.top
        logE(
            "StatusBarHeight: $StatusBarHeight  ScreenUtils.getScreenHeight(context) - windowHeight: ${
                ScreenUtils.getScreenHeight(
                    context
                ) - windowHeight
            }"
        )

        statusBarHeight = StatusBarHeight//ScreenUtils.getScreenHeight(context) - windowHeight

        // Make Dialog window span the entire screen
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buttonClose.setOnClickListener {
            this.dismiss()
        }
        contentView.setOnClickListener {
            if (clickOutSideToDismiss)
                this.dismiss()
        }
    }

    override fun show() {
        if (peekThroughViews.isNotEmpty()) drawPeekingViews() else drawShade()
        super.show()
    }

    private fun drawPeekingViews() {
        val bitmap = Bitmap.createBitmap(windowWidth, windowHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(context, R.color.tooltip_background_shade_dark))
        peekThroughViews.forEach {
            val viewBitmap = ScreenUtils.bitmapFromView(it)
            val xy = IntArray(2)
            it.getLocationOnScreen(xy)

            val top = if (positionGravity == Position.BELOW)
                positionY - it.measuredHeight// - 14
            else positionY - 14
            val bottom = if (positionGravity == Position.BELOW)
                positionY// - 9
            else positionY + it.measuredHeight - 9

//            val top = if (positionGravity == Position.BELOW)
//                xy[1] - it.measuredHeight// - 14
//            else xy[1]// - 14
//            val bottom = if (positionGravity == Position.BELOW)
//                xy[1]// - 9
//            else xy[1] + it.measuredHeight// - 9

            var paint: Paint? = null
            colorFilter?.let { colorF ->
                paint = Paint()
                val filter = PorterDuffColorFilter(colorF, PorterDuff.Mode.SRC_IN);
                paint?.setColorFilter(filter)
            }

            canvas.drawBitmap(
                viewBitmap,
                Rect(0, 0, it.measuredWidth, it.measuredHeight),
                Rect(
                    xy[0] - 0,
                    top,
                    xy[0] + it.measuredWidth,
                    bottom,
                ),
                paint
            )
        }

        contentView.background = BitmapDrawable(context.resources, bitmap)
    }

    private fun drawShade() {
        val bitmap = Bitmap.createBitmap(windowWidth, windowHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(context, R.color.tooltip_background_shade_default))
        contentView.background = BitmapDrawable(context.resources, bitmap)
    }

    /**
     * Add views that will be drawn onto the dialog shade
     */
    fun addPeekThroughView(view: View): ToolTipDialog {
        peekThroughViews.add(view)
        return this
    }

    /**
     * Set the position on screen for the dialog arrow to point to.
     * If Position is AUTO, this will set the dialog above
     * the point if y is below the halfway mark and below the point if the point is above halfway.
     */
    fun pointTo(x: Int, y: Int, position: Position = Position.AUTO): ToolTipDialog {
        val params = container.layoutParams as RelativeLayout.LayoutParams
        positionX = x
        positionY = y
        positionGravity = position
        adjustContainerMargin(x)

        if (position == Position.ABOVE || (position == Position.AUTO && y > windowHeight / 2 - statusBarHeight)) {
            // point is on the lower half of the screen, position dialog above
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.bottomMargin = windowHeight - y - statusBarHeight
            if (x >= 0) {
                pointArrowTo(downArrow, x)
            }
        } else {
            // point is on the upper half of the screen, position dialog below
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            params.topMargin = y - statusBarHeight
            if (x >= 0) {
                pointArrowTo(upArrow, x)
            }
        }

        container.layoutParams = params
        return this
    }

    private fun pointArrowTo(arrow: ImageView, x: Int) {
        val arrowParams = arrow.layoutParams as RelativeLayout.LayoutParams
        if (x > windowWidth / 2) {
            arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            arrowParams.rightMargin = windowWidth - x - arrowWidth / 2
        } else {
            arrowParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            arrowParams.leftMargin = x - arrowWidth / 2
        }
        arrow.layoutParams = arrowParams
        arrow.visibility = View.VISIBLE
    }

    private fun adjustContainerMargin(x: Int) {
        var leftMargin = context.resources.getDimension(R.dimen.tooltip_dialog_activity_margin)
        var rightMargin = leftMargin
        if (x > windowWidth - windowWidth / 3) {
            leftMargin = 30f
            rightMargin = 0f
        } else if (x < windowWidth / 3) {
            leftMargin = 0f
            rightMargin = 30f
        }
        val params = container.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = ScreenUtils.getPixels(context, leftMargin)
        params.rightMargin = ScreenUtils.getPixels(context, rightMargin)
        container.layoutParams = params
    }

    /**
     * Sets the y position of the top of the dialog box. This will not use any arrow pointers
     */
    fun setYPosition(y: Int): ToolTipDialog {
        val params = container.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        params.topMargin = y - statusBarHeight
        return this
    }

    fun content(content: String): ToolTipDialog {
        contentText.text = content
        return this
    }


    fun content(@StringRes contentResId: Int): ToolTipDialog {
        contentText.setText(contentResId)
        return this
    }

    fun buttonText(buttonText: String): ToolTipDialog {
        buttonClose.text = buttonText
        return this
    }

    fun showButton(isShowButton: Boolean = false): ToolTipDialog {
        buttonClose.isVisible = isShowButton
        return this
    }

    fun buttonText(@StringRes contentResId: Int): ToolTipDialog {
        buttonClose.setText(contentResId)
        return this
    }

    fun setClickOutsideToDismiss(outsideToDismiss: Boolean): ToolTipDialog {
        clickOutSideToDismiss = outsideToDismiss
        setCancelable(clickOutSideToDismiss)
        setCanceledOnTouchOutside(clickOutSideToDismiss)
        return this
    }

    fun setColorFilter(color: Int): ToolTipDialog {
        colorFilter = color
        return this
    }

    enum class Position {
        AUTO, ABOVE, BELOW
    }
}
