package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

data class MemoryCandidate(
    val memoryId: Long,
    val memoryTitle: String,
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

    companion object {
        fun buildNumberPickerDates(
            startAt: LocalDate?,
            endAt: LocalDate?,
        ): Map<Int, Map<Int, List<Int>>> {
            return if (startAt != null && endAt != null) {
                buildAvailableDates(startAt, endAt)
            } else {
                buildDefaultDates()
            }
        }

        private fun buildAvailableDates(
            startAt: LocalDate,
            endAt: LocalDate,
        ) = getAvailableYears(startAt, endAt).associateWith { year ->
            getAvailableMonths(year, startAt, endAt).associateWith { month ->
                getAvailableDays(year, month, startAt, endAt)
            }
        }

        private fun getAvailableYears(
            startAt: LocalDate,
            endAt: LocalDate,
        ): List<Int> {
            return (startAt.year..endAt.year).toList()
        }

        private fun getAvailableMonths(
            year: Int,
            startAt: LocalDate,
            endAt: LocalDate,
        ): List<Int> {
            return when {
                year == startAt.year && year == endAt.year -> (startAt.monthValue..endAt.monthValue).toList()
                year == startAt.year -> (startAt.monthValue..12).toList()
                year == endAt.year -> (1..endAt.monthValue).toList()
                else -> (1..12).toList()
            }
        }

        private fun getAvailableDays(
            year: Int,
            month: Int,
            startAt: LocalDate,
            endAt: LocalDate,
        ): List<Int> {
            val yearMonth = YearMonth.of(year, month)
            val daysInMonth = yearMonth.lengthOfMonth()

            return when {
                year == startAt.year && month == startAt.monthValue && year == endAt.year && month == endAt.monthValue -> {
                    (startAt.dayOfMonth..endAt.dayOfMonth).toList()
                }

                year == startAt.year && month == startAt.monthValue -> (startAt.dayOfMonth..daysInMonth).toList()
                year == endAt.year && month == endAt.monthValue -> (1..endAt.dayOfMonth).toList()
                else -> (1..daysInMonth).toList()
            }
        }

        private fun buildDefaultDates(): Map<Int, Map<Int, List<Int>>> {
            val defaultStart = LocalDate.now().minusYears(10)
            val defaultEnd = LocalDate.now().plusYears(10)
            return getDefaultYears(defaultStart, defaultEnd).associateWith { year ->
                getDefaultMonths().associateWith { month ->
                    getDefaultDays(year, month)
                }
            }
        }

        private fun getDefaultYears(
            startAt: LocalDate,
            endAt: LocalDate,
        ): List<Int> {
            return (startAt.year..endAt.year).toList()
        }

        private fun getDefaultMonths(): List<Int> {
            return (1..12).toList()
        }

        private fun getDefaultDays(
            year: Int,
            month: Int,
        ): List<Int> {
            val yearMonth = YearMonth.of(year, month)
            val daysInMonth = yearMonth.lengthOfMonth()
            return (1..daysInMonth).toList()
        }
    }
}
