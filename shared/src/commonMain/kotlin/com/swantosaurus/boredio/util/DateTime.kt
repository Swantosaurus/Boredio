package com.swantosaurus.boredio.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.toMillis() =
    toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

fun Long.toDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())