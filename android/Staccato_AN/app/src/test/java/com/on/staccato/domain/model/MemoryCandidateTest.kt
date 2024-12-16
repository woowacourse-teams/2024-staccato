package com.on.staccato.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryCandidateTest {
    // 아래부터 isDateWithinPeriod 테스트
    @Test
    fun `특정 날짜가 startAt과 endAt 사이에 포함되면 true 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearMiddle2024)
        assertTrue(actual)
    }

    @Test
    fun `특정 날짜와 startAt이 같으면 true 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearStart2024)
        assertTrue(actual)
    }

    @Test
    fun `특정 날짜와 endAt이 같으면 true 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearEnd2024)
        assertTrue(actual)
    }

    @Test
    fun `특정 날짜가 startAt 이전이면 false 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearEnd2023)
        assertFalse(actual)
    }

    @Test
    fun `특정 날짜가 endAt 이후면 false 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearStart2025)
        assertFalse(actual)
    }

    @Test
    fun `startAt과 endAt이 null이면 모든 날짜에 true 반환`() {
        // given
        val categoryWithoutPeriod =
            makeTestMemoryCandidate(
                startAt = null,
                endAt = null,
            )

        // when & then
        val actual1 = categoryWithoutPeriod.isDateWithinPeriod(yearEnd2023)
        val actual2 = categoryWithoutPeriod.isDateWithinPeriod(yearMiddle2024)
        val actual3 = categoryWithoutPeriod.isDateWithinPeriod(yearStart2025)

        assertTrue(actual1)
        assertTrue(actual2)
        assertTrue(actual3)
    }

    // 아래부터 getClosestDateTime 테스트
    @Test
    fun `현재 시간이 startAt과 endAt의 범위 내일 때 현재 시간을 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)
        val currentLocalDateTime = yearMiddle2024.atTime(13, 30)

        // when & then
        val actual = category.getClosestDateTime(currentLocalDateTime)

        assertEquals(currentLocalDateTime, actual)
    }

    @Test
    fun `현재 시간이 startAt보다 과거일 때 startAt의 정오를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)
        val currentLocalDateTime = yearEnd2023.atTime(13, 30)

        // when & then
        val actual = category.getClosestDateTime(currentLocalDateTime)
        val expected = yearStart2024.atTime(12, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `현재 시간이 endAt보다 미래일 때 endAt의 정오를 반환`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)
        val currentLocalDateTime = yearStart2025.atTime(13, 30)

        // when & then
        val actual = category.getClosestDateTime(currentLocalDateTime)
        val expected = yearEnd2024.atTime(12, 0)

        assertEquals(expected, actual)
    }
}
