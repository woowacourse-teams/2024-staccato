package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class MemoryCandidate(
    val categoryId: Long,
    val categoryTitle: String,
    val startAt: LocalDate?,
    val endAt: LocalDate?,
) {
    fun getClosestDateTime(date: LocalDateTime): LocalDateTime {
        if (startAt == null || endAt == null) return date

        val startDateTime = startAt.atStartOfDay()
        val endDateTime = endAt.atTime(23, 59)

        return when {
            date.isBefore(startDateTime) ->
                startDateTime.changeTimeToNoon()

            date.isAfter(endDateTime) ->
                endDateTime.changeTimeToNoon()

            else -> date
        }
    }

    fun isDateWithinPeriod(date: LocalDate): Boolean {
        if (startAt == null || endAt == null) return true
        return date in startAt..endAt
    }

    private fun LocalDateTime.changeTimeToNoon(): LocalDateTime = withHour(12).withMinute(0)
}
