package com.on.staccato.domain.model

import com.on.staccato.domain.model.MonthCalendar.Companion.of
import java.time.LocalDate

data class YearCalendar(private val yearToMonthCalender: Map<Int, MonthCalendar>) {
    companion object {
        private const val DEFAULT_HOUR: Int = 12
        private const val DEFAULT_MINUTE: Int = 0
        private const val DECADE_RANGE: Long = 10L
        val hours = (0 until 24).toList()

        fun of(
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): YearCalendar {
            if (periodStart != null && periodEnd != null) checkValid(periodStart, periodEnd)
            val yearRange = createYearRange(periodStart, periodEnd)
            return YearCalendar(
                yearRange.associateWith { year ->
                    of(year, periodStart, periodEnd)
                },
            )
        }

        private fun checkValid(
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ) {
            val isValidPeriod = periodStart.isBeforeOrEqual(periodEnd)
            require(isValidPeriod) { IllegalArgumentException("시작 날짜 ${periodStart}는 종료 날짜 ${periodEnd}보다 같거나 빨라야 합니다.") }
        }

        private fun LocalDate.isBeforeOrEqual(target: LocalDate?) = (isBefore(target) || isEqual(target))

        private fun createYearRange(
            periodStart: LocalDate?,
            periodEnd: LocalDate?,
        ): List<Int> =
            if (periodStart != null && periodEnd != null) {
                createYearsBetween(periodStart.year, periodEnd.year)
            } else {
                createDecadeOfYearsAroundCurrent()
            }

        private fun createYearsBetween(
            startYear: Int,
            endYear: Int,
        ): List<Int> {
            return (startYear..endYear).toList()
        }

        private fun createDecadeOfYearsAroundCurrent(): List<Int> {
            val now = LocalDate.now()
            val decadeAgo = now.minusYears(DECADE_RANGE)
            val decadeAhead = now.plusYears(DECADE_RANGE)
            return createYearsBetween(decadeAgo.year, decadeAhead.year)
        }
    }
}
