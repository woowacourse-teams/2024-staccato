package com.on.staccato.presentation.staccatocreation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.TARGET_MEMORY_ID
import com.on.staccato.domain.model.dummyMemoryCandidates
import com.on.staccato.domain.model.endDateOf2023
import com.on.staccato.domain.model.memoryCandidateWithId1
import com.on.staccato.domain.model.middleDateOf2024
import com.on.staccato.domain.model.startDateOf2024
import com.on.staccato.domain.model.targetMemoryCandidate
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
            viewModel.fetchMemoryCandidates()
            advanceUntilIdle()

            // then
            val actualMemoryCandidates = viewModel.memoryCandidates.getOrAwaitValue()
            assertEquals(dummyMemoryCandidates, actualMemoryCandidates)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 현재 날짜에서 선택 가능한 카테고리 후보 중 첫번째를 선택한다`() =
        runTest {
            // given
            viewModel.fetchMemoryCandidates()
            advanceUntilIdle()

            // when
            val currentLocalDate = middleDateOf2024.atStartOfDay()
            viewModel.initMemoryAndVisitedAt(0L, currentLocalDate)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
            val actualSelectableMemories = viewModel.selectableMemories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedMemory.getOrAwaitValue()

            val selectableMemories = dummyMemoryCandidates.filterBy(middleDateOf2024)
            val selectedMemory = selectableMemories.findByIdOrFirst(null)

            assertEquals(currentLocalDate, actualVisitedAt)
            assertEquals(selectableMemories, actualSelectableMemories)
            assertEquals(selectedMemory, actualSelectedMemory)
        }

    @Test
    fun `카테고리 ID가 0L이 아닐 때는 id로 카테고리를 선택하고, 현재와 가장 가까운 일시를 선택한다`() =
        runTest {
            // given
            viewModel.fetchMemoryCandidates()
            advanceUntilIdle()

            // when
            val currentVisitedAt = middleDateOf2024.atStartOfDay()
            viewModel.initMemoryAndVisitedAt(TARGET_MEMORY_ID, currentVisitedAt)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
            val actualSelectableMemories = viewModel.selectableMemories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedMemory.getOrAwaitValue()

            val closestVisitedAt = targetMemoryCandidate.getClosestDateTime(currentVisitedAt)
            val fixedSelectableMemories = CategoryCandidates.from(targetMemoryCandidate)
            val fixedSelectedMemory = targetMemoryCandidate

            assertEquals(closestVisitedAt, actualVisitedAt)
            assertEquals(fixedSelectableMemories, actualSelectableMemories)
            assertEquals(fixedSelectedMemory, actualSelectedMemory)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 일시가 바뀌면 memoryCandidate도 바뀐다`() =
        runTest {
            // given
            viewModel.fetchMemoryCandidates()

            val oldLocalDate = startDateOf2024.atStartOfDay()
            viewModel.initMemoryAndVisitedAt(TARGET_MEMORY_ID, oldLocalDate)

            // when
            val newLocalDate = endDateOf2023.atStartOfDay()
            viewModel.updateMemorySelectionBy(newLocalDate)

            // then
            val expectedSelectableMemories = CategoryCandidates.from(memoryCandidateWithId1)
            val expectedSelectedMemory = memoryCandidateWithId1

            val actualSelectableMemories = viewModel.selectableMemories.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedMemory.getOrAwaitValue()

            assertEquals(expectedSelectableMemories, actualSelectableMemories)
            assertEquals(expectedSelectedMemory, actualSelectedMemory)
        }

    private fun givenMemoryCandidatesReturnsSuccessWithDummyData() {
        coEvery { timelineRepository.getMemoryCandidates() } returns
            ResponseResult.Success(
                dummyMemoryCandidates,
            )
    }
}
