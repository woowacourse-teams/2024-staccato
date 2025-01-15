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
class CategoryCandidatesTest {
    @Test
    @Parameters(method = "parameters")
    fun `날짜를 포함하는 카테고리들을 필터링 하여 반환`(date: LocalDate) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val filteredCandidates = categoryCandidates.filterBy(date)

        // then
        val isEveryCandidatesContainDate = filteredCandidates.categoryCandidates.all { it.isDateWithinPeriod(date) }
        assertTrue(isEveryCandidatesContainDate)
    }

    @Test
    @Parameters(method = "existentCategoryIds")
    fun `findBy는 아이디가 일치하는 카테고리를 찾아 반환`(categoryId: Long) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findBy(categoryId)

        // then
        assertEquals(categoryId, actual?.categoryId)
    }

    @Test
    @Parameters(method = "nonExistentCategoryIds")
    fun `findBy는 일치하는 아이디가 없으면 null을 반환`(categoryId: Long) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findBy(categoryId)

        // then
        assertNull(actual)
    }

    @Test
    @Parameters(method = "existentCategoryIds")
    fun `findByIdOrFirst는 아이디가 일치하는 카테고리를 찾아 반환`(categoryId: Long) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findByIdOrFirst(categoryId)

        // then
        assertEquals(categoryId, actual?.categoryId)
    }

    @Test
    @Parameters(method = "nonExistentCategoryIds")
    fun `findByIdOrFirst는일치하는 아이디가 없으면 첫번째 카테고리를 반환`(categoryId: Long) {
        // given
        val categoryCandidates = dummyMemoryCandidates

        // when
        val actual = categoryCandidates.findByIdOrFirst(categoryId)
        val expected = memoryCandidateWithId1

        // then
        assertEquals(expected, actual)
    }

    private fun parameters(): List<LocalDate> = listOf(endDateOf2023, startDateOf2024, middleDateOf2024, endDateOf2024, startDateOf2025)

    private fun nonExistentCategoryIds(): List<Long> = listOf(80L, 999L, 1000L, 1234L)

    private fun existentCategoryIds(): List<Long> = listOf(1L, 2L, 3L, 4L)
}
