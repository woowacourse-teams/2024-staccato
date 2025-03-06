package com.on.staccato.presentation.categorycreation

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object DateConverter {
    fun convertLongToLocalDate(date: Long): LocalDate =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault(),
        ).toLocalDate()
}
