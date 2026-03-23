package vn.shb.data.entities

import android.annotation.SuppressLint
import android.icu.math.BigDecimal
import android.text.InputFilter
import android.text.Spanned
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

/**
 * số nguyên
 */
fun Double.roundForward(): Int {
    return ceil(this).toInt()
}

fun Double.roundBackWard(): Int {
    return floor(this).toInt()
}

fun Double.roundNearestInteger(): Int {
    return round(this).toInt()
}

fun roundDecimal(data: Double?): String {
    val decimalFormat = DecimalFormat("#.##")
    return decimalFormat.format(data)
}

fun Double.getBalance(): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    val formatter = DecimalFormat("#,##0.##", symbols)
    return formatter.format(this)
}

fun String.getBalanceFormatted(): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    val decimalCount = indexOf('.').takeIf { it >= 0 }?.let { length - it - 1 } ?: 0
    val pattern = if (decimalCount > 0) {
        "#,##0." + "0".repeat(decimalCount.coerceAtMost(2))
    } else {
        "#,##0"
    }

    val formatter = DecimalFormat(pattern, symbols)
    val parsed = this.toDoubleOrNull() ?: return this
    return formatter.format(parsed)
}


class DecimalDigitsInputFilter(
    private val digitsAfterZero: Int
) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source.isNullOrEmpty()) return null

        val newText = StringBuilder(dest)
            .replace(dstart, dend, source.subSequence(start, end).toString())
            .toString()

        // Chặn nhập nhiều hơn 1 dấu chấm
        if (newText.count { it == '.' } > 1) return ""

        val dotIndex = newText.indexOf('.')
        if (dotIndex >= 0) {
            val decimals = newText.substring(dotIndex + 1)
            // Nếu con trỏ ở cuối và đã có đủ số thập phân thì chặn
            if (decimals.length > digitsAfterZero && dstart > dotIndex) {
                return ""
            }
        }

        return null
    }
}
