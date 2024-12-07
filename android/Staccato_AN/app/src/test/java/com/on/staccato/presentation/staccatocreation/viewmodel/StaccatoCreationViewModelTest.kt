package com.on.staccato.presentation.staccatocreation.viewmodel

import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.TARGET_MEMORY_ID
import com.on.staccato.domain.model.dummyMemoryCandidates
import com.on.staccato.domain.model.newMemoryCandidate
import com.on.staccato.domain.model.targetMemoryCandidate
import com.on.staccato.domain.model.yearEnd2023
import com.on.staccato.domain.model.yearMiddle2024
import com.on.staccato.domain.model.yearStart2024
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.Executors

@RunWith(JUnit4::class)
class StaccatoCreationViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var timelineRepository: TimelineRepository

    @MockK
    private lateinit var staccatoRepository: StaccatoRepository

    @MockK
    private lateinit var imageRepository: ImageDefaultRepository

    @InjectMockKs
    private lateinit var viewModel: StaccatoCreationViewModel

    private val dispatcher =
        Executors
            .newSingleThreadExecutor()
            .asCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk(relaxed = true)
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.close()
    }

    @Test
    fun `viewModel 초기화 시 카테고리 후보를 불러온다`() =
        runTest {
            // given
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )

            // when
            viewModel.fetchMemoryCandidates()

            // when & then
            val actualMemoryCandidates = viewModel.memoryCandidates.getOrAwaitValue()
            assertEquals(dummyMemoryCandidates, actualMemoryCandidates)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 현재 날짜를 기준으로 일시와 카테고리 후보를 정한다`() {
        runTest {
            // given
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )
            viewModel.fetchMemoryCandidates()
        }
        // when
        val currentLocalDate = yearMiddle2024.atStartOfDay()
        viewModel.initMemoryAndVisitedAt(0L, currentLocalDate)

        // then
        val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
        val actualMemories = viewModel.selectableMemories.getOrAwaitValue()
        val actualMemory = viewModel.selectedMemory.getOrAwaitValue()

        val selectableMemories = dummyMemoryCandidates.filterCandidatesBy(yearMiddle2024)
        val selectedMemory = dummyMemoryCandidates.filterCandidatesBy(yearMiddle2024).first()

        assertEquals(currentLocalDate, actualVisitedAt)
        assertEquals(selectableMemories, actualMemories)
        assertEquals(selectedMemory, actualMemory)
    }

    @Test
    fun `카테고리 ID가 0L이 아닐 때는 특정 카테고리로 고정, 현재와 가장 가까운 일시를 선택한다`() {
        runTest {
            // given
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )
            viewModel.fetchMemoryCandidates()
        }
        // when
        val currentVisitedAt = yearMiddle2024.atStartOfDay()
        viewModel.initMemoryAndVisitedAt(TARGET_MEMORY_ID, currentVisitedAt)

        // then
        val actualVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
        val actualMemories = viewModel.selectableMemories.getOrAwaitValue()
        val actualMemory = viewModel.selectedMemory.getOrAwaitValue()

        val closestVisitedAt = targetMemoryCandidate.getClosestDateTime(currentVisitedAt)
        val fixedMemories = listOf(targetMemoryCandidate)
        val fixedMemory = targetMemoryCandidate

        assertEquals(closestVisitedAt, actualVisitedAt)
        assertEquals(fixedMemories, actualMemories)
        assertEquals(fixedMemory, actualMemory)
    }

    @Test
    fun `카테고리 ID가 0L일 때는 일시가 바뀌면 memoryCandidate도 바뀐다`() {
        runTest {
            // given
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )
            viewModel.fetchMemoryCandidates()
        }
        val oldLocalDate = yearStart2024.atStartOfDay()
        viewModel.initMemoryAndVisitedAt(TARGET_MEMORY_ID, oldLocalDate)

        // when
        val newLocalDate = yearEnd2023.atStartOfDay()
        viewModel.setMemoryCandidateByVisitedAt(newLocalDate)

        // then
        val expectedMemories = listOf(newMemoryCandidate)
        val expectedMemory = newMemoryCandidate

        val actualMemories = viewModel.selectableMemories.getOrAwaitValue()
        val actualMemory = viewModel.selectedMemory.getOrAwaitValue()

        assertEquals(expectedMemories, actualMemories)
        assertEquals(expectedMemory, actualMemory)
    }
}
