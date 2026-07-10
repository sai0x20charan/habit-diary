package com.charan.habitdiary.core.utils


import android.util.Log
import com.kizitonwose.calendar.core.minusDays
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaMonth
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
@OptIn(ExperimentalTime::class)
object DateUtil {

    fun getDaysOfWeek() : List<DayOfWeek> {
        return listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
    }
    fun getTodayDayAndDate(): String {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toJavaLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())

        return now.format(formatter)
    }

    fun defaultHabitFrequency() : List<DayOfWeek> {
        return listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )
    }

    @OptIn(ExperimentalTime::class)
    fun todayStartOfDay(): LocalDateTime {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun todayEndOfDay(): LocalDateTime {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        val endOfDay = today.atTime(23, 59, 59, 999_999_999)
        return endOfDay.toInstant(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun getCurrentDayOfWeek(): DayOfWeek {
        val todayIso = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .dayOfWeek
        return todayIso
    }

    fun mergeDateTime(date: LocalDate, time: LocalTime): LocalDateTime {
        val merged = LocalDateTime(
            date.year,
            date.month,
            date.day,
            time.hour,
            time.minute
        )

        return merged.toInstant(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }


    fun LocalTime.toFormattedString(is24HrFormat : Boolean) : String{
        val hour = if(is24HrFormat){
            this.hour
        } else {
            if(this.hour % 12 ==0) 12 else this.hour % 12
        }
        val minute = this.minute.toString().padStart(2,'0')
        val amPm = if(is24HrFormat) "" else if(this.hour <12) " AM" else " PM"
        return "$hour:$minute$amPm"
    }

    fun LocalDate.toFormattedString() : String{
        val month = this.month.name.lowercase().substring(0, 3).replaceFirstChar { it.uppercase() }
        return "$month ${this.day}, ${this.year}"
    }

    fun Long.toLocalDate() : LocalDate {
        return Instant.fromEpochMilliseconds(this)
            .toLocalDateTime(TimeZone.UTC)
            .date
    }

    @OptIn(ExperimentalTime::class)
    fun getCurrentMonth() : YearMonth {
        return YearMonth.now()
    }


    @OptIn(ExperimentalTime::class)
    fun getCurrentDate() : LocalDate {
        return LocalDate.now()
    }

    fun getCurrentTime() : LocalTime {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
    }

    fun getCurrentDateTime() : LocalDateTime {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun LocalDateTime.toFormattedString(is24HrFormat : Boolean) : String{
        val datePart = this.date.toFormattedString()
        val timePart = this.time.toFormattedString(is24HrFormat)
        return "$datePart, $timePart"
    }

    fun LocalDate.getStartOfDay() : LocalDateTime {
        val startOfDay = this.atStartOfDayIn(TimeZone.currentSystemDefault())
        return startOfDay.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun LocalDate.getEndOfDay() : LocalDateTime {
        val endOfDay = this.atTime(23, 59, 59, 999_999_999)
        return endOfDay.toInstant(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun Month.toLocale() : String{
        return this.toJavaMonth().getDisplayName(
            java.time.format.TextStyle.FULL,
            Locale.getDefault()
        )
    }

    fun DayOfWeek.toLocale(style : TextStyle) : String {
        return this.toJavaDayOfWeek().getDisplayName(
            style,
            Locale.getDefault()
        )
    }

    fun LocalDate.getOneYearBackRange() : Pair<LocalDate, LocalDate> {
        val targetDate = this.minusMonths(12)
        val startDate = targetDate.minusDays(14)
        val endDate = targetDate.plusDays(14)
        return Pair(startDate, endDate)
    }

    fun LocalDate.getThreeMonthsBackRange() : Pair<LocalDate, LocalDate> {
        val targetDate = this.minusMonths(3)
        val startDate = targetDate.minusDays(7)
        val endDate = targetDate.plusDays(7)
        return Pair(startDate, endDate)
    }

    fun LocalDate.getSixMonthsBackRange() : Pair<LocalDate, LocalDate> {
        val targetDate = this.minusMonths(6)
        val startDate = targetDate.minusDays(14)
        val endDate = targetDate.plusDays(14)
        return Pair(startDate, endDate)
    }
}
