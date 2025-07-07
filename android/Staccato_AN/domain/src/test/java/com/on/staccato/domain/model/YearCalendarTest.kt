package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(JUnitParamsRunner::class)
class YearCalendarTest {
    @Test
    fun `기간의 시작일과 종료일 사이에 해당하는 달력을 반환한다`() {
        // given
        val startYear = 2024
        val startMonth = 12
        val startDate = 1
        val start = LocalDate.of(startYear, startMonth, startDate)
        val end = start.plusYears(1)

        // when
        val actualCalendar = YearCalendar.of(start, end)

        // then
        assertEquals(startYear, actualCalendar.selectedYear)
        assertEquals(startMonth, actualCalendar.selectedMonth)
        assertEquals(startDate, actualCalendar.selectedDate)

        val defaultHour = 12
        val defaultMinute = 0
        val dateTime =
            LocalDateTime.of(startYear, startMonth, startDate, defaultHour, defaultMinute)
        assertEquals(defaultHour, actualCalendar.selectedHour)
        assertEquals(dateTime, actualCalendar.selectedDateTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `기간의 시작일이 종료일보다 늦으면 예외가 발생한다`() {
        // given
        val periodStart = LocalDate.of(2024, 12, 31)
        val periodEnd = LocalDate.of(2024, 1, 1)
        // when & then
        YearCalendar.of(periodStart, periodEnd)
    }

    @Test
    fun `달력의 모든 년도를 반환한다`() {
        val periodStart = LocalDate.of(2023, 2, 2)
        val periodEnd = LocalDate.of(2025, 2, 1)
        val calendar = YearCalendar.of(periodStart, periodEnd)

        val actual = calendar.getAvailableYears()
        val expected = (2023..2025).toList()
        assertEquals(expected, actual)
    }

    @Test
    fun `선택된 년도의 월을 반환한다`() {
        // given
        val startYear = 2023
        val startMonth = 5
        val periodStart = LocalDate.of(startYear, startMonth, 2)
        val periodEnd = LocalDate.of(2025, 6, 1)
        val actualCalendar = YearCalendar.of(periodStart, periodEnd)

        // when
        val actualMonths = actualCalendar.getAvailableMonths()
        val expectedMonths = (startMonth..12).toList()
        assertEquals(startYear, actualCalendar.selectedYear)
        assertEquals(expectedMonths, actualMonths)

        // then
        assertEquals(startYear, actualCalendar.selectedYear)
        assertEquals(expectedMonths, actualMonths)
    }

    @Test
    fun `방문일이 주어지면 해당 날짜를 기준 앞,뒤로 100년씩의 범위를 가진 YearCalendar가 생성된다`() {
        // given
        val visitedAt = LocalDate.of(2025, 1, 7)
        val expectedYearRange = (visitedAt.minusYears(100).year..visitedAt.plusYears(100).year).toList()

        // when
        val yearCalendar = YearCalendar.from(visitedAt)

        // then
        assertEquals(expectedYearRange, yearCalendar.getAvailableYears())
    }

    @Test
    fun `시작일과 종료일이 주어지면 해당 기간의 연도 범위를 가진 YearCalendar가 생성된다`() {
        // given
        val periodStart = LocalDate.of(2023, 1, 1)
        val periodEnd = LocalDate.of(2025, 12, 31)
        val expectedYearRange = (2023..2025).toList()

        // when
        val yearCalendar = YearCalendar.of(periodStart, periodEnd)

        // then
        assertEquals(expectedYearRange, yearCalendar.getAvailableYears())
    }

    @Test
    fun `시작일과 종료일을 지정하지 않으면 현재 날짜를 기준으로 앞,뒤로 100년씩의 범위를 가진 YearCalendar가 생성된다`() {
        // given
        val now = LocalDate.of(2025, 1, 1)
        val expectedYearRange = (now.minusYears(100).year..now.plusYears(100).year).toList()

        // when
        val yearCalendar = YearCalendar.of()
        println(yearCalendar.getAvailableYears())

        // then
        assertEquals(expectedYearRange, yearCalendar.getAvailableYears())
    }

    @Test
    fun `선택된 연도를 변경하면 해당 연도의 사용 가능한 월 목록을 반환한다`() {
        // given
        val periodStart = LocalDate.of(2024, 4, 1)
        val periodEnd = LocalDate.of(2024, 8, 30)
        val yearCalendar = YearCalendar.of(periodStart, periodEnd)

        // when
        yearCalendar.changeSelectedYear(2024)

        // then
        assertEquals((4..8).toList(), yearCalendar.getAvailableMonths())
    }

    @Test
    fun `선택된 연월을 변경하면 해당 월의 사용 가능한 일자 목록을 반환한다`() {
        // given
        val periodStart = LocalDate.of(2024, 4, 15)
        val periodEnd = LocalDate.of(2025, 5, 15)
        val yearCalendar = YearCalendar.of(periodStart, periodEnd)

        // when
        yearCalendar.changeSelectedYear(2024)
        yearCalendar.changeSelectedMonth(4)
        yearCalendar.changeSelectedDate(15)
        yearCalendar.changeSelectedHour(20)

        // then
        assertEquals((15..30).toList(), yearCalendar.getAvailableDates())
        assertEquals(LocalDateTime.of(2024, 4, 15, 20, 0), yearCalendar.selectedDateTime)
    }

    @Test
    fun `initSelectedDateTime으로 날짜를 초기화할 수 있다`() {
        // given
        val yearCalendar = YearCalendar.of()
        val initDateTime = LocalDateTime.of(2024, 5, 15, 14, 0)

        // when
        yearCalendar.initSelectedDateTime(initDateTime)

        // then
        assertEquals(initDateTime, yearCalendar.selectedDateTime)
    }
}
