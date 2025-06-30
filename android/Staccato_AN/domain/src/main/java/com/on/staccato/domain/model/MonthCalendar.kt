package com.on.staccato.domain.model

import com.on.staccato.domain.model.DateCalendar.Companion.of
import java.time.LocalDate

data class MonthCalendar private constructor(private val monthToDateCalendar: Map<Int, DateCalendar>) {
    fun getAvailableMonths(): List<Int> = monthToDateCalendar.keys.toList()

    fun getAvailableDates(selectedMonth: Int): List<Int> =
        monthToDateCalendar[selectedMonth]?.availableDates ?: throw IllegalArgumentException()

    fun getClosestMonth(targetMonth: Int): Int {
        val availableMonths = getAvailableMonths()
        return targetMonth.coerceIn(availableMonths.first(), availableMonths.last())
    }

    fun findDateCalendar(selectedMonth: Int): DateCalendar? = monthToDateCalendar[selectedMonth]

    companion object {
        private const val JANUARY = 1
        private const val DECEMBER = 12
        private val fullMonths: List<Int> = (JANUARY..DECEMBER).toList()

        fun of(
            year: Int,
            periodStart: LocalDate? = null,
            periodEnd: LocalDate? = null,
        ): MonthCalendar {
            if (periodStart != null && periodEnd != null) checkValid(year, periodStart, periodEnd)
            val monthRange = createMonthRangeIn(year, periodStart, periodEnd)
            return MonthCalendar(
                monthRange.associateWith { month ->
                    of(year, month, periodStart, periodEnd)
                },
            )
        }

        private fun checkValid(
            year: Int,
            periodStart: LocalDate,
            periodEnd: LocalDate,
        ) {
            val isBetweenPeriod = (year in periodStart.year..periodEnd.year)
            val isValidPeriod = periodStart.isBeforeOrEqual(periodEnd)
            require(isBetweenPeriod) { IllegalArgumentException("${year}년은 ${periodStart.year}년과 ${periodEnd.year}년 사이여야 합니다.") }
            require(isValidPeriod) { IllegalArgumentException("시작 날짜 ${periodStart}는 종료 날짜 ${periodEnd}보다 같거나 빨라야 합니다.") }
        }

        private fun LocalDate.isBeforeOrEqual(target: LocalDate?) = (isBefore(target) || isEqual(target))

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
                isInSameYear(
                    year,
                    periodStart,
                    periodEnd,
                ) -> createMonthsBetween(periodStart.monthValue, periodEnd.monthValue)

                isStartYear(year, periodStart) ->
                    createMonthsBetween(
                        periodStart.monthValue,
                        DECEMBER,
                    )

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
