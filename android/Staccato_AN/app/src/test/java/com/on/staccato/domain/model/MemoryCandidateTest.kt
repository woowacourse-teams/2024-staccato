package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class MemoryCandidateTest {
    @Test
    @Parameters(method = "validLocalDateParameters")
    fun `타겟 날짜가 기간 내면 true를 반환`(targetLocalDate: LocalDate) {
        // given
        val category =
            makeTestMemoryCandidate(
                startAt = startDateOf2024,
                endAt = endDateOf2024,
            )
        // when
        val actual = category.isDateWithinPeriod(targetLocalDate)
        // then
        assertTrue(actual)
    }

    @Test
    fun `타겟 날짜가 기간보다 과거라면 false를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(
                startAt = startDateOf2024,
                endAt = endDateOf2024,
            )
        val targetLocalDate = endDateOf2023
        // when
        val actual = category.isDateWithinPeriod(targetLocalDate)
        // then
        assertFalse(actual)
    }

    @Test
    fun `타겟 날짜가 기간보다 미래라면 false를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(
                startAt = startDateOf2024,
                endAt = endDateOf2024,
            )
        val targetLocalDate = startDateOf2025
        // when
        val actual = category.isDateWithinPeriod(targetLocalDate)
        // then
        assertFalse(actual)
    }

    @Test
    @Parameters(method = "localDateParameters")
    fun `기간 없는 카테고리는 모든 날짜에 true를 반환`(date: LocalDate) {
        // given
        val categoryWithoutPeriod =
            makeTestMemoryCandidate(
                startAt = null,
                endAt = null,
            )
        // when
        val actual = categoryWithoutPeriod.isDateWithinPeriod(date)
        // then
        assertTrue(actual)
    }

    @Test
    fun `타겟 일시가 기간 범위 내면 그대로 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = startDateOf2024, endAt = endDateOf2024)
        val targetLocalDateTime = middleDateOf2024.atTime(13, 30)
        // when
        val actual = category.getClosestDateTime(targetLocalDateTime)
        // then
        assertEquals(targetLocalDateTime, actual)
    }

    @Test
    fun `타겟 일시가 기간보다 과거라면 시작일의 정오를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = startDateOf2024, endAt = endDateOf2024)
        val targetLocalDateTime = endDateOf2023.atTime(13, 30)
        // when
        val actual = category.getClosestDateTime(targetLocalDateTime)
        val expected = startDateOf2024.atTime(12, 0)
        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `타겟 일시가 기간보다 미래라면 종료일의 정오를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = startDateOf2024, endAt = endDateOf2024)
        val targetLocalDateTime = startDateOf2025.atTime(13, 30)
        // when
        val actual = category.getClosestDateTime(targetLocalDateTime)
        val expected = endDateOf2024.atTime(12, 0)
        // then
        assertEquals(expected, actual)
    }

    private fun validLocalDateParameters(): List<LocalDate> = listOf(startDateOf2024, middleDateOf2024, endDateOf2024)

    private fun localDateParameters(): List<LocalDate> =
        listOf(
            LocalDate.now(),
            LocalDate.now().minusYears(10),
            LocalDate.now().plusYears(10),
        )
}
