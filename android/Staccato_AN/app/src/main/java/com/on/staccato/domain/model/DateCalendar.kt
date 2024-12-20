package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class DateCalendar(private val availableDates: List<Int>) {
    companion object {
        private const val FIRST_DATE = 1

        fun of(
            year: Int,
            month: Int,
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): DateCalendar {
            val dateRange = createDateRangeIn(year, month, periodStart, periodEnd)
            return DateCalendar(dateRange)
        }

        private fun createDateRangeIn(
            year: Int,
            month: Int,
            periodStart: LocalDate?,
            periodEnd: LocalDate?,
        ): List<Int> {
            val lastDateOfMonth = YearMonth.of(year, month).lengthOfMonth()
            return if (periodStart != null && periodEnd != null) {
                calculateDateRangeIn(year, month, lastDateOfMonth, periodStart, periodEnd)
            } else {
                createDatesBetween(FIRST_DATE, lastDateOfMonth)
            }
        }

        private fun calculateDateRangeIn(
            year: Int,
            month: Int,
            lastDateOfMonth: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ): List<Int> =
            when {
                isInSameMonth(year, month, periodStart, periodEnd) -> createDatesBetween(periodStart.dayOfMonth, periodEnd.dayOfMonth)
                isStartMonth(year, month, periodStart) -> createDatesBetween(periodStart.dayOfMonth, lastDateOfMonth)
                isEndMonth(year, month, periodEnd) -> createDatesBetween(1, periodEnd.dayOfMonth)
                else -> createDatesBetween(1, lastDateOfMonth)
            }

        private fun isInSameMonth(
            year: Int,
            month: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ) = isStartMonth(year, month, periodStart) && isEndMonth(year, month, periodEnd)

        private fun isStartMonth(
            year: Int,
            month: Int,
            periodStart: LocalDate,
        ) = (year == periodStart.year && month == periodStart.monthValue)

        private fun isEndMonth(
            year: Int,
            month: Int,
            periodEnd: LocalDate,
        ) = (year == periodEnd.year && month == periodEnd.monthValue)

        private fun createDatesBetween(
            startDate: Int,
            endDate: Int,
        ) = (startDate..endDate).toList()
    }
}
