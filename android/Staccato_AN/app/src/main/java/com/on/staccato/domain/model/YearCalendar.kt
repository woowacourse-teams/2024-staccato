package com.on.staccato.domain.model

import com.on.staccato.domain.model.MonthCalendar.Companion.of
import java.time.LocalDate

data class YearCalendar(private val yearToMonthCalender: Map<Int, MonthCalendar>) {
    companion object {
        private const val DECADE_RANGE: Long = 10L

        fun of(
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): YearCalendar {
            val yearRange = createYearRange(periodStart, periodEnd)
            return YearCalendar(
                yearRange.associateWith { year ->
                    of(year, periodStart, periodEnd)
                },
            )
        }

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
