package com.on.staccato.presentation.staccatoupdate.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.TARGET_MEMORY_ID
import com.on.staccato.domain.model.TARGET_STACCATO_ID
import com.on.staccato.domain.model.dummyMemoryCandidates
import com.on.staccato.domain.model.newMemoryCandidate
import com.on.staccato.domain.model.targetMemoryCandidate
import com.on.staccato.domain.model.targetStaccato
import com.on.staccato.domain.model.yearEnd2023
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.MainDispatcherRule
import com.on.staccato.presentation.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StaccatoUpdateViewModelTest {
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
    private lateinit var viewModel: StaccatoUpdateViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `수정하려는 스타카토와 추억 후보를 불러와 스타카토, 일시 및 추억 선택 값을 초기화 한다`() {
        runTest {
            // given
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )
            coEvery { staccatoRepository.getStaccato(TARGET_MEMORY_ID) } returns
                ResponseResult.Success(targetStaccato)

            // when
            viewModel.fetchTargetData(staccatoId = TARGET_STACCATO_ID)
        }

        // then
        // 스타카토 관련 값 검사
        val actualPlaceName = viewModel.placeName.getOrAwaitValue()
        val actualAddress = viewModel.address.getOrAwaitValue()

        Assert.assertEquals(targetStaccato.placeName, actualPlaceName)
        Assert.assertEquals(targetStaccato.address, actualAddress)

        // 일시 선택을 위한 값 검사
        val expectedSelectableMemories =
            dummyMemoryCandidates.filterCandidatesBy(targetStaccato.visitedAt.toLocalDate())
        val actualSelectableMemories = viewModel.selectableMemories.getOrAwaitValue()
        Assert.assertEquals(expectedSelectableMemories, actualSelectableMemories)

        // 추억 선택을 위한 값 검사
        val actualMemoryCandidates = viewModel.memoryCandidates.getOrAwaitValue()
        val actualSelectedMemory = viewModel.selectedMemory.getOrAwaitValue()
        val actualSelectedVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()

        Assert.assertEquals(dummyMemoryCandidates, actualMemoryCandidates)
        Assert.assertEquals(targetMemoryCandidate, actualSelectedMemory)
        Assert.assertEquals(targetStaccato.visitedAt, actualSelectedVisitedAt)
    }

    @Test
    fun `일시가 바뀌면 그에 따라 선택 가능 카테고리, 선택된 카테고리를 업데이트 한다`() {
        // given
        runTest {
            coEvery { timelineRepository.getMemoryCandidates() } returns
                ResponseResult.Success(
                    dummyMemoryCandidates,
                )
            coEvery { staccatoRepository.getStaccato(TARGET_MEMORY_ID) } returns
                ResponseResult.Success(targetStaccato)
            viewModel.fetchTargetData(staccatoId = TARGET_STACCATO_ID)
        }

        // when : 현재 선택된 카테고리 범위 밖의 날짜로 바뀌면
        val newLocalDate = yearEnd2023.atStartOfDay()
        viewModel.setMemoryCandidateByVisitedAt(newLocalDate)

        // then : 바뀐 날짜 기준으로 유효한 값을 업데이트한다
        val expectedMemories = listOf(newMemoryCandidate)
        val actualMemories = viewModel.selectableMemories.getOrAwaitValue()

        val expectedMemory = newMemoryCandidate
        val actualMemory = viewModel.selectedMemory.getOrAwaitValue()

        Assert.assertEquals(expectedMemories, actualMemories)
        Assert.assertEquals(expectedMemory, actualMemory)
    }
}
