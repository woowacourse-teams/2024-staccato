package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class DateCalendarTest {
    @Test
    fun `기간의 시작과 끝을 정하지 않으면 2024년 2월의 모든 일을 반환한다`() {
        // 2024년은 윤년이라 2월이 29일까지 있다
        val expected = (1..29).toList()
        val actual = DateCalendar.of(2024, 2).availableDates
        assertEquals(expected, actual)
    }

    @Test
    fun `기간의 시작과 끝을 정하지 않으면 2025년 2월의 모든 일을 반환한다`() {
        // 2025년은 2월 28일까지 있다
        val expected = (1..28).toList()
        val actual = DateCalendar.of(2025, 2).availableDates
        assertEquals(expected, actual)
    }

    @Test
    fun `기간의 시작과 끝이 정해져 있으면 기간 안에 속하는 일들을 반환한다`() {
        // given
        val startDate = LocalDate.of(2023, 2, 1)
        val endDate = LocalDate.of(2025, 12, 31)

        // when
        val expected = (1..28).toList()
        val actual = DateCalendar.of(2023, 2, startDate, endDate).availableDates

        // then
        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `날짜가 기간을 벗어나면 예외를 반환한다`() {
        val periodStart = LocalDate.of(2023, 12, 1)
        val periodEnd = LocalDate.of(2024, 1, 10)
        DateCalendar.of(2024, 2, periodStart, periodEnd)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `기간의 시작이 끝보다 늦으면 예외를 반환한다`() {
        val start = LocalDate.of(2024, 2, 2)
        val end = LocalDate.of(2024, 2, 1)
        DateCalendar.of(2024, 2, start, end)
    }

    @Test
    @Parameters(method = "dateParametersFrom10to20")
    fun `범위 안에 있다면 해당 날짜를 그대로 반환한다`(date: Int) {
        val actual = december10thTo20thCalendar.getClosestDate(date)
        assertEquals(date, actual)
    }

    @Test
    @Parameters(method = "dateParametersSmallerThan10")
    fun `범위보다 작으면 가장 첫번째 날짜를 반환한다`(date: Int) {
        // given
        val firstDate = 10

        // then
        val actual = december10thTo20thCalendar.getClosestDate(date)
        assertEquals(firstDate, actual)
    }

    @Test
    @Parameters(method = "dateParametersGraterThan20")
    fun `범위보다 크면 가장 마지막 날짜를 반환한다`(date: Int) {
        // given
        val lastDate = 20

        // then
        val actual = december10thTo20thCalendar.getClosestDate(date)
        assertEquals(lastDate, actual)
    }

    private fun dateParametersFrom10to20(): List<Int> = listOf(10, 15, 20)

    private fun dateParametersSmallerThan10(): List<Int> = listOf(1, 5, 9)

    private fun dateParametersGraterThan20(): List<Int> = listOf(21, 25, 30)
}
