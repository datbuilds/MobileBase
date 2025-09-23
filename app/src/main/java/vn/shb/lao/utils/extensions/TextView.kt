package vn.shb.lao.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import vn.shb.lao.R
import vn.shb.lao.utils.extensions.DateTimeHelper.Companion.isSameDay
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale

fun TextView.setGradientTextColor(context: Context, vararg colorRes: Int) {
    val floatArray = ArrayList<Float>(colorRes.size)
    for (i in colorRes.indices) {
        floatArray.add(i, i.toFloat() / (colorRes.size - 1))
    }
    val textShader: Shader = LinearGradient(
        0f,
        0f,
        0f,
        this.height.toFloat(),
        colorRes.map { ContextCompat.getColor(context, it) }.toIntArray(),
        floatArray.toFloatArray(),
        Shader.TileMode.CLAMP
    )
    this.paint.shader = textShader
}

fun TextView.handlerLink(
    underLine: Boolean? = false,
    onLinkClicked: ((url: String) -> Unit?)? = null
) {
    val s: Spannable = SpannableString(text)
    val spans = s.getSpans(0, s.length, URLSpan::class.java)
    for (span in spans) {
        val start = s.getSpanStart(span)
        val end = s.getSpanEnd(span)
        s.removeSpan(span)
        val spanNew = URLSpanNoUnderline(span.url, onLinkClicked, underLine)
        s.setSpan(spanNew, start, end, 0)
    }
    if (movementMethod == null || movementMethod !is LinkMovementMethod) {
        movementMethod = LinkMovementMethod.getInstance()
    }
    text = s
}

private class URLSpanNoUnderline(
    url: String?,
    val onLinkClicked: ((url: String) -> Unit?)? = null,
    val underLine: Boolean? = false
) : URLSpan(url) {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = underLine ?: false
    }

    override fun onClick(widget: View) {
        onLinkClicked?.invoke(url) ?: run {
            super.onClick(widget)
        }
    }
}

@Suppress("DEPRECATION")
fun TextView.fromHtml(html: String?) {
    this.text = when {
        html == null -> {
            SpannableString("")
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, MyImageGetter(this), null)
        }

        else -> {
            Html.fromHtml(html)
        }
    }
}

private class MyImageGetter(private val textView: TextView) : Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val resourceId = this.textView.context.resources.getIdentifier(
            source,
            "drawable",
            this.textView.context.packageName
        )
        val drawable = ContextCompat.getDrawable(this.textView.context, resourceId)

        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        return drawable ?: BitmapDrawable(
            this.textView.context.resources,
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        )
    }
}

// Start set drawable icon
fun TextView.setDrawableIcon(
    context: Context,
    drawableResId: Int,
    drawablePosition: DrawablePosition
) {
    val drawable: Drawable? = ContextCompat.getDrawable(context, drawableResId)
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    when (drawablePosition) {
        DrawablePosition.START -> setCompoundDrawables(drawable, null, null, null)
        DrawablePosition.TOP -> setCompoundDrawables(null, drawable, null, null)
        DrawablePosition.END -> setCompoundDrawables(null, null, drawable, null)
        DrawablePosition.BOTTOM -> setCompoundDrawables(null, null, null, drawable)
    }
}

enum class DrawablePosition {
    START, TOP, END, BOTTOM
}
// End set drawable icon
//---------------------------------------
// Set drawable theo state

fun TextView.setDrawableIconByState(context: Context, state: IconState) {
    val drawable: Drawable? = when (state) {
        IconState.NORMAL -> ContextCompat.getDrawable(context, R.drawable.ic_dot)
        IconState.SUCCESS -> ContextCompat.getDrawable(context, R.drawable.ic_valid_checked)
        IconState.FAILURE -> ContextCompat.getDrawable(context, R.drawable.ic_valid_faild)
    }

    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    setCompoundDrawables(drawable, null, null, null)
}

fun TextView.setDateAndCompareToday(inputDate: String) {
    val inputDateFormat = SimpleDateFormat(DateTimeHelper.DATE_FORMAT, Locale.getDefault())
    val currentDate = Calendar.getInstance().time
    val parsedDate: Date = try {
        inputDateFormat.parse(inputDate)
    } catch (e: Exception) {
        null
    } ?: return

    val isToday = isSameDay(currentDate, parsedDate)

    text = if (isToday) {
        "Hôm nay"
    } else {
        SimpleDateFormat(DateTimeHelper.DATE_FORMAT, Locale.getDefault()).format(parsedDate)
    }
}

fun TextView.formatAmountVND(amount: BigDecimal = BigDecimal.ZERO) {
    val formatter: NumberFormat = DecimalFormat("#,###")
    val formattedAmount = formatter.format(amount).replace(".", ",")
    text = formattedAmount
}

fun TextView.formatAmount(amount: BigDecimal = BigDecimal.ZERO, ccy: String = "VND") {
    try {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val formattedAmount = when {
            ccy == "VND" -> {
                "${formatter.format(amount)} VND".replace(".", ",")
            }

            amount == BigDecimal.ZERO -> {
                "0 $ccy"
            }

            else -> {
                "${formatUSD(amount)} $ccy"
            }
        }
        text = formattedAmount
    } catch (ex: Exception) {
        text = "0 $ccy"
    }
}

fun formatUSD(amount: BigDecimal = BigDecimal.ZERO): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
    currencyFormat.applyPattern("#,##0.00")
    currencyFormat.currency = Currency.getInstance("USD")

    return currencyFormat.format(amount)
}

fun formatAmount(amount: BigDecimal = BigDecimal.ZERO, ccy: String = "VND"): String {
    val formatter: NumberFormat = DecimalFormat("#,###")
    val formattedAmount = when {
        ccy == "VND" -> {
            "${formatter.format(amount)} VND".replace(".", ",")
        }

        amount == BigDecimal.ZERO -> {
            "0 $ccy"
        }

        else -> {
            "${formatUSD(amount)} $ccy"
        }
    }
    return formattedAmount
}

fun getGreetingMessage(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return when {
        hour in 5..11 -> "Chào buổi sáng,\uD83D\uDC4B"
        hour == 12 || (hour in 13..17) -> "Chào buổi chiều,\uD83D\uDC4B"
        hour in 18..20 -> "Chào buổi tối,\uD83D\uDC4B"
        else -> "Chúc ngủ ngon,\uD83D\uDC4B"
    }
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.setCustomSpannable(content: String, onClick: (() -> Unit)) {

    val spannable = SpannableStringBuilder()

    val start = spannable.length
    spannable.append(content)
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
            }
        },
        start,
        start + content.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
    isClickable = true

    // Xử lý hiệu ứng pressed thủ công vì dialog không support hiệu ứng này
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                text = SpannableStringBuilder(content).apply {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@setCustomSpannable.context,
                                R.color.color_hotlineClick
                            )
                        ), // màu khi nhấn
                        0,
                        content.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                text = spannable
            }
        }
        return@setOnTouchListener false // vẫn cho click hoạt động
    }
}

enum class IconState {
    NORMAL, SUCCESS, FAILURE
}