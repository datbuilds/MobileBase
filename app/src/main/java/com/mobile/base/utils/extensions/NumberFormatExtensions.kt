package com.mobile.base.utils.extensions

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Extension functions để format số liệu
 */

/**
 * Format số tiền với dấu phẩy ngăn cách hàng nghìn
 */
fun Float.formatCurrency(): String {
    return NumberFormat.getNumberInstance(Locale("vi", "VN")).format(this)
}

/**
 * Format số tiền với đơn vị triệu đồng
 */
fun Float.formatCurrencyInMillions(): String {
    val formatter = DecimalFormat("#,##0.0")
    return "${formatter.format(this)} trđ"
}

/**
 * Format số tiền với đơn vị nghìn đồng
 */
fun Float.formatCurrencyInThousands(): String {
    val formatter = DecimalFormat("#,##0")
    return "${formatter.format(this)} nghìn"
}

/**
 * Format phần trăm
 */
fun Float.formatPercentage(): String {
    val formatter = DecimalFormat("#,##0.00")
    return "${formatter.format(this)}%"
}

/**
 * Format số nguyên với dấu phẩy ngăn cách
 */
fun Int.formatNumber(): String {
    return NumberFormat.getNumberInstance(Locale("vi", "VN")).format(this)
}

/**
 * Format số thập phân với số chữ số thập phân tùy chỉnh
 */
fun Float.formatDecimal(decimalPlaces: Int = 2): String {
    val pattern = "#,##0.${"0".repeat(decimalPlaces)}"
    val formatter = DecimalFormat(pattern)
    return formatter.format(this)
}

/**
 * Format số tiền cho DTI calculation
 */
fun Float.formatDTIAmount(): String {
    return when {
        this >= 1000 -> formatCurrencyInMillions()
        this >= 1 -> formatCurrencyInThousands()
        else -> formatCurrency()
    }
}