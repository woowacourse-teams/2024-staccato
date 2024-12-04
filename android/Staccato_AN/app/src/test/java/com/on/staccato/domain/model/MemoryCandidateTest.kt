package com.on.staccato.domain.model

import org.junit.Assert.assertEquals
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
        val expected = true

        assertEquals(expected, actual)
    }

    @Test
    fun `특정 날짜와 startAt이 같으면 true 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearStart2024)
        val expected = true

        assertEquals(expected, actual)
    }

    @Test
    fun `특정 날짜와 endAt이 같으면 true 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearEnd2024)
        val expected = true

        assertEquals(expected, actual)
    }

    @Test
    fun `특정 날짜가 startAt 이전이면 false 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearEnd2023)
        val expected = false

        assertEquals(expected, actual)
    }

    @Test
    fun `특정 날짜가 endAt 이후면 false 반환`() {
        val category =
            makeTestMemoryCandidate(
                startAt = yearStart2024,
                endAt = yearEnd2024,
            )

        val actual = category.isDateWithinPeriod(yearStart2025)
        val expected = false

        assertEquals(expected, actual)
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
        val expected1 = true
        val actual2 = categoryWithoutPeriod.isDateWithinPeriod(yearMiddle2024)
        val expected2 = true
        val actual3 = categoryWithoutPeriod.isDateWithinPeriod(yearStart2025)
        val expected3 = true

        assertEquals(expected1, actual1)
        assertEquals(expected2, actual2)
        assertEquals(expected3, actual3)
    }

    // 아래부터 getClosestDateTime 테스트
    @Test
    fun `2024년 중 2023년 마지막 날 12시와 가장 가까운 날짜 및 시간을 반환한다`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)

        // when & then
        val actual = category.getClosestDateTime(yearEnd2023.atTime(12, 0))
        val expected = yearStart2024.atTime(0, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `2024년 중 2024년 7월 1일 12시와 가장 가까운 날짜 및 시간을 반환한다`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)

        // when & then
        val actual = category.getClosestDateTime(yearMiddle2024.atTime(12, 0))
        val expected = yearMiddle2024.atTime(12, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `2024년 중 2025년 첫번째 날 12시와 가장 가까운 날짜 및 시간을 반환한다`() {
        // given
        val category =
            makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearEnd2024)

        // when & then
        val actual = category.getClosestDateTime(yearStart2025.atTime(12, 0))
        val expected = yearEnd2024.atTime(0, 0)

        assertEquals(expected, actual)
    }
}
