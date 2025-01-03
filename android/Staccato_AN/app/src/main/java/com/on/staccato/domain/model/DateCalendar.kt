package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class DateCalendar private constructor(val availableDates: List<Int>) {
    fun getClosestDate(target: Int): Int = target.coerceIn(availableDates.first(), availableDates.last())

    companion object {
        private const val FIRST_DATE = 1

        fun of(
            year: Int,
            month: Int,
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): DateCalendar {
            if (periodStart != null && periodEnd != null) checkValid(year, month, periodStart, periodEnd)
            val dateRange = createDateRangeIn(year, month, periodStart, periodEnd)
            return DateCalendar(dateRange)
        }

        private fun checkValid(
            year: Int,
            month: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ) {
            val startDate = LocalDate.of(year, month, periodStart.dayOfMonth)
            val endDate = LocalDate.of(year, month, periodEnd.dayOfMonth)
            val isBetweenPeriod = periodStart.isBeforeOrEqual(startDate) && periodEnd.isAfterOrEqual(endDate)
            val isValidPeriod = periodStart.isBeforeOrEqual(periodEnd)
            require(isBetweenPeriod) { IllegalArgumentException("${year}년 ${month}월은 $periodStart $periodEnd 사이여야 합니다.") }
            require(isValidPeriod) { IllegalArgumentException("$year $month $periodStart $periodEnd 기간이 유효하지 않습니다.") }
        }

        private fun LocalDate.isAfterOrEqual(target: LocalDate?) = (isAfter(target) || isEqual(target))

        private fun LocalDate.isBeforeOrEqual(target: LocalDate?) = (isBefore(target) || isEqual(target))

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
                isInSameMonth(
                    year,
                    month,
                    periodStart,
                    periodEnd,
                ) -> createDatesBetween(periodStart.dayOfMonth, periodEnd.dayOfMonth)

                isStartMonth(year, month, periodStart) ->
                    createDatesBetween(
                        periodStart.dayOfMonth,
                        lastDateOfMonth,
                    )

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
