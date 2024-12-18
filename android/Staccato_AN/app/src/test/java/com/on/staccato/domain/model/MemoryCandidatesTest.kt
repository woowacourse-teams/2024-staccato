package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class MemoryCandidatesTest {
    @Test
    @Parameters(method = "parameters")
    fun `날짜를 포함하는 카테고리들을 필터링 하여 반환`(date: LocalDate) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.filterCandidatesBy(date)

        // then
        val isEveryCandidatesContainDate = actual.all { it.isDateWithinPeriod(date) }
        assertTrue(isEveryCandidatesContainDate)
    }

    @Test
    fun `아이디가 일치하는 카테고리를 찾아 반환`() {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findCandidatesBy(TARGET_MEMORY_ID)
        val expected = targetMemoryCandidate

        // then
        assertEquals(expected, actual)
    }

    @Test
    @Parameters(method = "nonExistentCategoryIds")
    fun `아이디가 일치하는 카테고리가 없으면 null을 반환`(categoryId: Long) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findCandidatesBy(categoryId)

        // then
        assertNull(actual)
    }

    private fun parameters(): List<LocalDate> = listOf(yearEnd2023, yearStart2024, yearMiddle2024, yearEnd2024, yearStart2025)

    private fun nonExistentCategoryIds(): List<Long> = listOf(80L, 999L, 1000L, 1234L)
}
