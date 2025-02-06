package com.on.staccato.domain.model

import com.on.staccato.domain.model.MonthCalendar.Companion.of
import java.time.LocalDate
import java.time.LocalDateTime

data class YearCalendar private constructor(private val yearToMonthCalender: Map<Int, MonthCalendar>) {
    var selectedYear: Int = yearToMonthCalender.keys.first()
        private set
    var selectedMonth: Int = getAvailableMonths().first()
        private set
    var selectedDate: Int = getAvailableDates().first()
        private set
    var selectedHour: Int = DEFAULT_HOUR
        private set

    var selectedDateTime: LocalDateTime = LocalDateTime.of(selectedYear, selectedMonth, selectedDate, selectedHour, DEFAULT_MINUTE)
        get() = LocalDateTime.of(selectedYear, selectedMonth, selectedDate, selectedHour, DEFAULT_MINUTE)
        private set

    fun getAvailableYears(): List<Int> = yearToMonthCalender.keys.toList()

    fun getAvailableMonths(): List<Int> = yearToMonthCalender[selectedYear]?.getAvailableMonths() ?: throw IllegalArgumentException()

    fun getAvailableDates(): List<Int> =
        yearToMonthCalender[selectedYear]?.getAvailableDates(selectedMonth)
            ?: throw IllegalArgumentException()

    fun initSelectedDateTime(dateTime: LocalDateTime) {
        selectedYear = dateTime.year
        selectedMonth = dateTime.monthValue
        selectedDate = dateTime.dayOfMonth
        selectedHour = dateTime.hour
    }

    fun changeSelectedYear(newYear: Int) {
        selectedYear = newYear
        resetSelectedMonths()
    }

    fun changeSelectedMonth(newMonth: Int) {
        selectedMonth = newMonth
        resetSelectedDates()
    }

    fun changeSelectedDate(newDate: Int) {
        selectedDate = newDate
    }

    fun changeSelectedHour(newHour: Int) {
        selectedHour = newHour
    }

    private fun resetSelectedMonths() {
        findMonthCalendar()?.let { newMonthCalender ->
            selectedMonth = newMonthCalender.getClosestMonth(selectedMonth)
            resetSelectedDates()
        }
    }

    private fun resetSelectedDates() {
        findDateCalendar()?.let { newDateCalender ->
            selectedDate = newDateCalender.getClosestDate(selectedDate)
        }
    }

    private fun findMonthCalendar(): MonthCalendar? = yearToMonthCalender[selectedYear]

    private fun findDateCalendar(): DateCalendar? = findMonthCalendar()?.findDateCalendar(selectedMonth)

    companion object {
        private const val DEFAULT_HOUR: Int = 12
        private const val DEFAULT_MINUTE: Int = 0
        private const val HUNDRED_RANGE: Long = 100L
        val hours = (0 until 24).toList()

        fun from(visitedAt: LocalDate): YearCalendar {
            val yearRange = createHundredOfYearsAroundCurrent(visitedAt)
            return YearCalendar(
                yearRange.associateWith { year ->
                    of(year)
                },
            )
        }

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
                createHundredOfYearsAroundCurrent(LocalDate.now())
            }

        private fun createYearsBetween(
            startYear: Int,
            endYear: Int,
        ): List<Int> {
            return (startYear..endYear).toList()
        }

        private fun createHundredOfYearsAroundCurrent(targetDate: LocalDate): List<Int> {
            val hundredYearsAgo = targetDate.minusYears(HUNDRED_RANGE)
            val hundredYearsAhead = targetDate.plusYears(HUNDRED_RANGE)
            return createYearsBetween(hundredYearsAgo.year, hundredYearsAhead.year)
        }
    }
}
