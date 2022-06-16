package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.MIN_DAYS_LEFT_TO_UPLOAD_CERTIFICATE
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max

fun Instant.toCalendarDay(): CalendarDay? {
    val date = toLocalDateTimeOrNull()
    return CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
}

fun Instant.toLocalDateTimeOrNull(): LocalDateTime =
    LocalDateTime.ofInstant(this, ZoneId.systemDefault())

fun LocalDateTime.toInstant(): Instant = atZone(ZoneId.systemDefault()).toInstant()
fun LocalDateTime.getWeekday(): String =
    dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

fun Instant.isSameDay(): Boolean {
    val now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
    val toCompare = toLocalDateTimeOrNull().truncatedTo(ChronoUnit.DAYS)
    return now.isEqual(toCompare)
}

fun String.daysLeftFrom(maxDaysLeft: Int): Int? {
    return try {
        val today = LocalDate.now()
        val contractSignedAtDate = Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDate()
        val pastDays = ChronoUnit.DAYS.between(contractSignedAtDate, today).toInt()
        max(MIN_DAYS_LEFT_TO_UPLOAD_CERTIFICATE, maxDaysLeft.minus(pastDays))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun String.toInstant(): Instant? = try {
    Instant.parse(this)
} catch (ex: DateTimeParseException) {
    null
}

fun String.toLocalDateTimeOrNull(): LocalDateTime? = toInstant()?.toLocalDateTimeOrNull()

fun String.toLocalDateOrNull() = try {
    LocalDate.parse(this)
} catch (e: DateTimeParseException) {
    null
}

fun String.toLocalTimeOrNull() = try {
    LocalTime.parse(this)
} catch (e: DateTimeParseException) {
    null
}

fun LocalDateTime.formatOrNull(formatter: DateTimeFormatter) = try {
    format(formatter)
} catch (e: DateTimeException) {
    null
}

fun LocalDate.formatOrNull(formatter: DateTimeFormatter) = try {
    format(formatter)
} catch (e: DateTimeException) {
    null
}

fun LocalTime.formatOrNull(formatter: DateTimeFormatter) = try {
    format(formatter)
} catch (e: DateTimeException) {
    null
}
