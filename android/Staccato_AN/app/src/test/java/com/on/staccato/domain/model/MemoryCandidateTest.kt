package com.on.staccato.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MemoryCandidateTest {
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
}
