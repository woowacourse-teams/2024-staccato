package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class MemoryCandidatesTest {
    @Test
    @Parameters(method = "parameters")
    fun `date를 포함하는 후보들을 필터링해 반환`(date: LocalDate) {
        // given
        val memoryCandidates = dummyMemoryCandidates

        // when
        val selectableMemoryCandidate = memoryCandidates.getValidMemoryCandidate(date)

        // then
        val isEveryCandidatesContainDate = selectableMemoryCandidate.all { it.isDateWithinPeriod(date) }
        assertTrue(isEveryCandidatesContainDate)
    }

    @Test
    fun `아이디가 정확히 일치하는 memoryCandidate를 찾아 반환`() {
        // given
        val memoryCandidates = dummyMemoryCandidates

        // when
        val actual = memoryCandidates.getValidMemoryCandidate(TARGET_MEMORY_ID)
        val expected = listOf(targetMemoryCandidate)

        // then
        assertEquals(expected, actual)
    }

    private fun parameters(): List<LocalDate> = listOf(yearEnd2023, yearStart2024, yearMiddle2024, yearEnd2024, yearStart2025)
}
