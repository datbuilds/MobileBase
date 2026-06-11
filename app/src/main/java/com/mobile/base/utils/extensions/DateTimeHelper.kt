package com.mobile.base.utils.extensions

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateTimeHelper {
    companion object {

        const val DATE_FORMAT_2 = "yyyyMMdd"
        const val DATE_NO_FORMAT = "ddMMyyyy"
        const val DATE_FORMAT = "dd/MM/yyyy"
        private const val TIME_FORMAT = "HH:mm, MMM dd"

        fun convertSKTime(input: String): String {
            val dateTime = LocalDateTime.parse(input)
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return dateTime.format(outputFormatter)
        }

        fun parseDate(dateString: String): Date {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            return dateFormat.parse(dateString) ?: Date(0)
        }

        fun isSameDay(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }

        fun getCalendarFromString(dateString: String): Calendar {
            return try {
                val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                val date: Date = sdf.parse(dateString) ?: Date()

                val calendar = Calendar.getInstance()
                calendar.time = date

                calendar
            } catch (ex: Exception) {
                ex.printStackTrace()
                Calendar.getInstance()
            }
        }

        fun getDateFromCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
            return currentDate.format(formatter)
        }

        fun toDisplayDate(text: String): String {
            return try {
                val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val dateTime = LocalDateTime.parse(text, inputFormatter)
                dateTime.format(outputFormatter)
            } catch (e: DateTimeParseException) {
                text // Nếu lỗi parse, trả về chuỗi gốc để tránh crash
            }
        }

        fun Int.getTimeLess(): String {
            return try {
                val minutes = TimeUnit.SECONDS.toMinutes(this.toLong())
                val seconds = this - TimeUnit.MINUTES.toSeconds(minutes)
                String.format("%02d:%02d", minutes, seconds)
            } catch (e: Exception) {
                "00:00"
            }
        }

        fun Int.toMMSS(): String {
            val minutes = this / 60
            val remainingSeconds = this % 60
            return String.format("%02d:%02d", minutes, remainingSeconds)
        }

        fun Int.toFormattedTime(totalTime: Int = 0, isFormatCountdown: Boolean = false): String {
            return if (isFormatCountdown) {
                if (totalTime <= 60)
                    this.toMMSS()
                else {
                    this.toHHMMSS()
                }
            } else {
                this.formatNumber()
            }
        }

        private fun Int.toHHMMSS(): String {
            val hours = this / 3600
            val minutes = (this % 3600) / 60
            val remainingSeconds = this % 60

            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }

        fun Int.formatNumber() = if (this < 10) {
            this.toString().padStart(2, '0')
        } else {
            this.toString()
        }

        fun getDurationBreakdown(
            milliseconds: Long,
            useDotDivide: Boolean = false,
            showDay: Boolean = true,
            showZero: Boolean = false
        ): String {
            var millis = milliseconds
            if (showDay) {
                val units = if (useDotDivide)
                    arrayOf(":", ":", ":", ":")
                else
                    arrayOf(" ngày ", " giờ ", " phút ", " giây ")

                val values = LongArray(units.size)
                require(millis >= 0) { "Duration must be greater than zero!" }
                values[0] = TimeUnit.MILLISECONDS.toDays(millis)
                millis -= TimeUnit.DAYS.toMillis(values[0])
                values[1] = TimeUnit.MILLISECONDS.toHours(millis)
                millis -= TimeUnit.HOURS.toMillis(values[1])
                values[2] = TimeUnit.MILLISECONDS.toMinutes(millis)
                millis -= TimeUnit.MINUTES.toMillis(values[2])
                values[3] = TimeUnit.MILLISECONDS.toSeconds(millis)
                val sb = StringBuilder(64)
                var startPrinting = true

                for (i in units.indices) {
                    if (startPrinting && values[i] != 0L || !startPrinting || i > 0) {
                        startPrinting = false
                        sb.append(String.format("%02d", values[i]))
                        if (i < 3 || !useDotDivide)
                            sb.append(units[i])
                    }
                }
                return sb.toString()
            } else {
                val units = if (useDotDivide)
                    arrayOf(":", ":", ":")
                else
                    arrayOf(" giờ ", " phút ", " giây ")

                val values = LongArray(units.size)
                require(millis >= 0) { "Duration must be greater than zero!" }
                values[0] = TimeUnit.MILLISECONDS.toHours(millis)
                millis -= TimeUnit.HOURS.toMillis(values[0])
                values[1] = TimeUnit.MILLISECONDS.toMinutes(millis)
                millis -= TimeUnit.MINUTES.toMillis(values[1])
                values[2] = TimeUnit.MILLISECONDS.toSeconds(millis)
                val sb = StringBuilder()
                var startPrinting = true

                for (i in units.indices) {
                    if (showZero) {
                        if (startPrinting || !startPrinting || i > 0) {
                            startPrinting = false
                            sb.append(String.format("%02d", values[i]))
                            if (i < 2 || !useDotDivide)
                                sb.append(units[i])
                        }
                    } else
                        if (startPrinting && values[i] != 0L || !startPrinting || i > 0) {
                            startPrinting = false
                            sb.append(String.format("%02d", values[i]))
                            if (i < 2 || !useDotDivide)
                                sb.append(units[i])
                        }
                }
                return sb.toString()
            }
        }

        fun getDurationBreakdown(milliseconds: Long): String {
            require(milliseconds >= 0) { "Duration must be greater than or equal to zero!" }

            val units = arrayOf("ngày", "giờ", "phút", "giây")
            val values = arrayOf(
                TimeUnit.MILLISECONDS.toDays(milliseconds),
                TimeUnit.MILLISECONDS.toHours(milliseconds) % 24,
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60,
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
            )

            val parts = values
                .mapIndexedNotNull { index, value ->
                    if (value > 0 || index == values.size - 1) {
                        "$value ${units[index]}"
                    } else {
                        null
                    }
                }

            return parts.joinToString(" ")
        }
    }
}