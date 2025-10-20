package vn.shb.data.entities

import android.annotation.SuppressLint
import android.icu.math.BigDecimal
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

/**
 * số thập phân
 */

@SuppressLint("NewApi")
fun Double.roundDecimal(): Double {
    return this.toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
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

    val pattern = if (this % 1 == 0.0) "#,##0" else "#,##0.00"
    val formatter = DecimalFormat(pattern, symbols)
    return formatter.format(this)
}
