package com.on.staccato.presentation.staccatocreation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.TARGET_CATEGORY_ID
import com.on.staccato.domain.model.dummyCategoryCandidates
import com.on.staccato.domain.model.endDateOf2023
import com.on.staccato.domain.model.categoryCandidateWithId1
import com.on.staccato.domain.model.middleDateOf2024
import com.on.staccato.domain.model.startDateOf2024
import com.on.staccato.domain.model.targetCategoryCandidate
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.MainDispatcherRule
import com.on.staccato.presentation.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class StaccatoCreationViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var timelineRepository: TimelineRepository

    @MockK
    private lateinit var staccatoRepository: StaccatoRepository

    @MockK
    private lateinit var imageRepository: ImageDefaultRepository

    @InjectMockKs
    private lateinit var viewModel: StaccatoCreationViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        givenMemoryCandidatesReturnsSuccessWithDummyData()
    }

    @Test
    fun `viewModel 초기화 시 카테고리 후보를 불러온다`() =
        runTest {
            // when
            viewModel.fetchCategoryCandidates()
            advanceUntilIdle()

            // then
            val actualMemoryCandidates = viewModel.categoryCandidates.getOrAwaitValue()
            assertEquals(dummyCategoryCandidates, actualMemoryCandidates)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 현재 날짜에서 선택 가능한 카테고리 후보 중 첫번째를 선택한다`() =
        runTest {
            // given
            viewModel.fetchCategoryCandidates()
            advanceUntilIdle()

            // when
            val currentLocalDate = middleDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(0L, currentLocalDate)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
            val actualSelectableMemories = viewModel.selectableCategories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedCategory.getOrAwaitValue()

            val selectableMemories = dummyCategoryCandidates.filterBy(middleDateOf2024)
            val selectedMemory = selectableMemories.findByIdOrFirst(null)

            assertEquals(currentLocalDate, actualVisitedAt)
            assertEquals(selectableMemories, actualSelectableMemories)
            assertEquals(selectedMemory, actualSelectedMemory)
        }

    @Test
    fun `카테고리 ID가 0L이 아닐 때는 id로 카테고리를 선택하고, 현재와 가장 가까운 일시를 선택한다`() =
        runTest {
            // given
            viewModel.fetchCategoryCandidates()
            advanceUntilIdle()

            // when
            val currentVisitedAt = middleDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(TARGET_CATEGORY_ID, currentVisitedAt)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
            val actualSelectableMemories = viewModel.selectableCategories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedCategory.getOrAwaitValue()

            val closestVisitedAt = targetCategoryCandidate.getClosestDateTime(currentVisitedAt)
            val fixedSelectableMemories = CategoryCandidates.from(targetCategoryCandidate)
            val fixedSelectedMemory = targetCategoryCandidate

            assertEquals(closestVisitedAt, actualVisitedAt)
            assertEquals(fixedSelectableMemories, actualSelectableMemories)
            assertEquals(fixedSelectedMemory, actualSelectedMemory)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 일시가 바뀌면 memoryCandidate도 바뀐다`() =
        runTest {
            // given
            viewModel.fetchCategoryCandidates()

            val oldLocalDate = startDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(TARGET_CATEGORY_ID, oldLocalDate)

            // when
            val newLocalDate = endDateOf2023.atStartOfDay()
            viewModel.updateCategorySelectionBy(newLocalDate)

            // then
            val expectedSelectableMemories = CategoryCandidates.from(categoryCandidateWithId1)
            val expectedSelectedMemory = categoryCandidateWithId1

            val actualSelectableMemories = viewModel.selectableCategories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedCategory.getOrAwaitValue()

            assertEquals(expectedSelectableMemories, actualSelectableMemories)
            assertEquals(expectedSelectedMemory, actualSelectedMemory)
        }

    private fun givenMemoryCandidatesReturnsSuccessWithDummyData() {
        coEvery { timelineRepository.getCategoryCandidates() } returns
            ResponseResult.Success(
                dummyCategoryCandidates,
            )
    }
}
