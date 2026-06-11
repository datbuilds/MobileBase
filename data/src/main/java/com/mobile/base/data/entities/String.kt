package com.mobile.base.data.entities

import android.content.Intent
import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.View
import androidx.core.net.toUri
import java.math.BigDecimal
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.util.regex.Pattern
import kotlin.text.ifEmpty
import kotlin.text.trim

fun String.isNumeric(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun String?.formatAmount(): String {
    if (this.isNullOrBlank()) return ""
    return try {
        val clean = this.replace(",", "").trim()
        val number = clean.toLong()
        amount(amount = number.toBigDecimal())
    } catch (e: Exception) {
        "0"
    }
}

fun amount(amount: BigDecimal = BigDecimal.ZERO, ccy: String = "VND"): String {
    return try {
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
        formattedAmount
    } catch (ex: Exception) {
        "0 $ccy"
    }
}

fun formatUSD(amount: BigDecimal = BigDecimal.ZERO): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
    currencyFormat.applyPattern("#,##0.00")
    currencyFormat.currency = Currency.getInstance("USD")

    return currencyFormat.format(amount)
}

fun String?.toSafeInt(): Int = try {
    this?.toInt() ?: 0
} catch (e: Exception) {
    0
}

fun String.toSafeLong(): Long = try {
    this.replace(",", "").toLong()
} catch (e: Exception) {
    0L
}

fun String.containsBoldTag(): Boolean {
    return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N &&
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString() != this
}

fun String.isAlphabet(): Boolean = matches("[a-zA-Z]+( +[a-zA-Z]+)*".toRegex())

fun String.validPhone(): Boolean {
    return this.length >= 10 && !this.contains(" ") &&
            (matches("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$".toRegex())
                    || matches("^[+]?[\\d]+([\\-][\\d]+)*\\d\$".toRegex()))
}

fun String.validEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.validUrl() = Patterns.WEB_URL.matcher(this).matches()

fun deAccentEnglishCharacter(str: String): String {
    val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
    val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(nfdNormalizedString).replaceAll("")
        .replace("Đ", "D")
        .replace("đ", "d")
}

fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    val messageDigest = md.digest(this.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashtext = no.toString(16)
    while (hashtext.length < 32) {
        hashtext = "0$hashtext"
    }
    return hashtext
}

fun String.formatWith(vararg args: Any?): String {
    return String.format(this, *args)
}

fun String.maskPhoneNumber(visibleDigits: Int): String {
    val digitsOnly = this.replace(Regex("\\D"), "")
    val numberOfStars = digitsOnly.length - visibleDigits
    return "*".repeat(numberOfStars) + digitsOnly.takeLast(visibleDigits)
}

fun String.boldSubstrings(vararg substrings: String): SpannableStringBuilder {
    val spannable = SpannableStringBuilder(this)

    for (substring in substrings) {
        val startIndex = this.indexOf(substring)
        if (startIndex != -1) {
            val endIndex = startIndex + substring.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
    }

    return spannable
}

fun String.removeAccent(): String {
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D")
}

fun String.colorizeSubstring(substring: String, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    val startIndex = indexOf(substring, ignoreCase = true)

    if (startIndex != -1) {
        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            startIndex + substring.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannableString
}

fun randomString(length: Int = 15): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun String.getInitials(): String {
    val name = this.ifEmpty { "SHB" }
    val parts = name.trim().split("\\s+".toRegex()) // tách theo khoảng trắng
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(1).uppercase() // chỉ có 1 từ
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

fun String.makeClickableSpan(): ClickableSpan {
    return object : ClickableSpan() {
        override fun onClick(widget: View) {
        }
    }
}


