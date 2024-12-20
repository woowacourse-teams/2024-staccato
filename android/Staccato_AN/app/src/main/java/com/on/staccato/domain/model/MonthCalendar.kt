package com.on.staccato.domain.model

import com.on.staccato.domain.model.DateCalendar.Companion.of
import java.time.LocalDate

data class MonthCalendar(private val monthToDateCalendar: Map<Int, DateCalendar>) {
    companion object {
        private const val JANUARY = 1
        private const val DECEMBER = 12
        private val fullMonths: List<Int> = (JANUARY..DECEMBER).toList()

        fun of(
            year: Int,
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): MonthCalendar {
            val monthRange = createMonthRangeIn(year, periodStart, periodEnd)
            return MonthCalendar(
                monthRange.associateWith { month ->
                    of(year, month, periodStart, periodEnd)
                },
            )
        }

        private fun createMonthRangeIn(
            year: Int,
            periodStart: LocalDate?,
            periodEnd: LocalDate?,
        ): List<Int> =
            if (periodStart != null && periodEnd != null) {
                calculateMonthRangeIn(year, periodStart, periodEnd)
            } else {
                fullMonths
            }

        private fun calculateMonthRangeIn(
            year: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ): List<Int> =
            when {
                isInSameYear(year, periodStart, periodEnd) -> createMonthsBetween(periodStart.monthValue, periodEnd.monthValue)
                isStartYear(year, periodStart) -> createMonthsBetween(periodStart.monthValue, DECEMBER)
                isEndYear(year, periodEnd) -> createMonthsBetween(JANUARY, periodEnd.monthValue)
                else -> fullMonths
            }

        private fun isInSameYear(
            year: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ) = isStartYear(year, periodStart) && isEndYear(year, periodEnd)

        private fun isStartYear(
            year: Int,
            periodStart: LocalDate,
        ) = year == periodStart.year

        private fun isEndYear(
            year: Int,
            periodEnd: LocalDate,
        ) = year == periodEnd.year

        private fun createMonthsBetween(
            startMonth: Int,
            endMonth: Int,
        ) = (startMonth..endMonth).toList()
    }
}
