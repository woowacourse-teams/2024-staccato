package com.on.staccato.presentation.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate =
    LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault(),
    ).toLocalDate()
