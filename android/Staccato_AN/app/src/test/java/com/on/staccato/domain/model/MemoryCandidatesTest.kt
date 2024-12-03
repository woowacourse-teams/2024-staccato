package com.on.staccato.domain.model

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(JUnitParamsRunner::class)
class MemoryCandidatesTest {
    @Test
    @Parameters(method = "parameters")
    fun `getValidMemoryCandidate는 date를 포함하는 후보만 필터링`(date: LocalDate) {
        // given
        val memoryCandidates = dummyMemoryCandidates

        // when
        val selectableMemoryCandidate = memoryCandidates.getValidMemoryCandidate(date)

        // then
        val actual = selectableMemoryCandidate.all { it.isDateWithinPeriod(date) }
        assertTrue(actual)
    }

    private fun parameters(): List<LocalDate> = listOf(yearEnd2023, yearStart2024, yearMiddle2024, yearEnd2024, yearStart2025)
}
