package com.swantosaurus.boredio.android.ui.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.swantosaurus.boredio.android.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
fun Int.relativeToDay(): String =
    relativeToDay(LocalContext.current)

fun Int.relativeToDay(ctx: Context): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return when (this) {
        0 -> "Today"
        1 -> "Yday"
        else -> {
            val dayOfWeek = today.dayOfWeek.minus(this.toLong())
            val calendarDay = today.minus(DatePeriod(days = this)).dayOfMonth
            dayOfWeek.toHumanString(ctx).take(1) + " " + calendarDay
        }
    }
}

@Composable
fun DayOfWeek.toHumanString(): String =
    toHumanString(LocalContext.current)

fun DayOfWeek.toHumanString(ctx: Context): String {
    return when (this) {
        DayOfWeek.MONDAY -> ctx.getString(R.string.monday)
        DayOfWeek.TUESDAY -> ctx.getString(R.string.tuesday)
        DayOfWeek.WEDNESDAY -> ctx.getString(R.string.wednesday)
        DayOfWeek.THURSDAY -> ctx.getString(R.string.thursday)
        DayOfWeek.FRIDAY -> ctx.getString(R.string.friday)
        DayOfWeek.SATURDAY -> ctx.getString(R.string.saturday)
        DayOfWeek.SUNDAY -> ctx.getString(R.string.sunday)
    }
}
