package vn.shb.lao.utils.extensions

import android.annotation.SuppressLint
import android.icu.math.BigDecimal
import java.text.DecimalFormat
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
