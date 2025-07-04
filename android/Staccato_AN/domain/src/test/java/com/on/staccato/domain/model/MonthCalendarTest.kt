package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class MonthCalendarTest {
    @Test
    fun `기간의 시작과 끝을 정하지 않으면 2024년의 모든 월을 반환한다`() {
        val monthCalendarOf2024 = MonthCalendar.of(year = 2024)
        val actual = monthCalendarOf2024.getAvailableMonths()
        val expected = (1..12).toList()
        assertEquals(expected, actual)
    }

    @Test
    fun `기간의 시작과 끝이 정해져 있으면 기간 안에 속하는 월들을 반환한다`() {
        val allMonthsOf2024Calendar =
            MonthCalendar.of(
                year = 2024,
                periodStart = LocalDate.of(2024, 1, 1),
                periodEnd = LocalDate.of(2025, 6, 1),
            )
        val actual = allMonthsOf2024Calendar.getAvailableMonths()
        val expected = (1..12).toList()
        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `날짜가 기간을 벗어나면 예외를 반환한다`() {
        val periodStart = LocalDate.of(2023, 12, 1)
        val periodEnd = LocalDate.of(2024, 1, 10)
        MonthCalendar.of(2025, periodStart, periodEnd)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `기간의 시작이 끝보다 늦으면 예외를 반환한다`() {
        val periodStart = LocalDate.of(2024, 2, 2)
        val periodEnd = LocalDate.of(2024, 2, 1)
        MonthCalendar.of(2024, periodStart, periodEnd)
    }

    @Test
    fun `달력에서 특정 달에 해당하는 일들을 반환한다`() {
        val december = 12
        val actual = december1stTo15thMonthCalendar.getAvailableDates(december)
        val expected = (1..15).toList()

        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `달력에 특정 달이 없으면 예외를 던진다`() {
        val january = 1
        december1stTo15thMonthCalendar.getAvailableDates(january)
    }

    @Test
    @Parameters(method = "monthParametersFrom4to8")
    fun `4월부터 8월 사이라면 해당 월을 그대로 반환한다`(targetMonth: Int) {
        val actual = aprilToAugustCalendar.getClosestMonth(targetMonth)

        assertEquals(targetMonth, actual)
    }

    @Test
    @Parameters(method = "monthParametersSmallerThan4")
    fun `범위보다 작으면 가장 첫번째 월을 반환한다`(targetMonth: Int) {
        val firstMonth = 4
        val actual = aprilToAugustCalendar.getClosestMonth(targetMonth)

        assertEquals(firstMonth, actual)
    }

    @Test
    @Parameters(method = "monthParametersGraterThan8")
    fun `범위보다 크면 가장 마지막 월을 반환한다`(targetMonth: Int) {
        val lastMonth = 8
        val actual = aprilToAugustCalendar.getClosestMonth(targetMonth)

        assertEquals(lastMonth, actual)
    }

    @Test
    fun `입력된 월의 DateCalendar를 반환한다`() {
        val actual = aprilToAugustCalendar.findDateCalendar(6)
        val expected = juneDateCalendar
        assertEquals(expected, actual)
    }

    private fun monthParametersFrom4to8(): List<Int> = listOf(4, 5, 6, 7, 8)

    private fun monthParametersSmallerThan4(): List<Int> = listOf(1, 2, 3)

    private fun monthParametersGraterThan8(): List<Int> = listOf(9, 10, 11, 12)
}
