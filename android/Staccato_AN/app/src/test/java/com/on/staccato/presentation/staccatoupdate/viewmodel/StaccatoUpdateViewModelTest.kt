package com.on.staccato.presentation.staccatoupdate.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.TARGET_STACCATO_ID
import com.on.staccato.domain.model.dummyMemoryCandidates
import com.on.staccato.domain.model.makeTestStaccato
import com.on.staccato.domain.model.newMemoryCandidate
import com.on.staccato.domain.model.targetMemoryCandidate
import com.on.staccato.domain.model.yearEnd2023
import com.on.staccato.domain.model.yearEnd2024
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

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun `수정하려는 스타카토, 추억 후보를 불러온 뒤 뷰모델의 스타카토, 일시 및 추억 관련 데이터를 초기화 한다`() =
        runTest {
            // given : 뷰모델 메서드 반환값 지정
            val targetStaccato = givenTargetStaccatoWithRepositorySetup()

            // when : TARGET_STACCATO_ID에 맞는 스타카토를 불러온다.
            viewModel.fetchTargetData(staccatoId = TARGET_STACCATO_ID)
            advanceUntilIdle()

            // then : 뷰모델의 LiveData가 초기화 된다.
            // 스타카토 관련 데이터 검사
            val actualPlaceName = viewModel.placeName.getOrAwaitValue()
            val actualAddress = viewModel.address.getOrAwaitValue()

            assertEquals(targetStaccato.placeName, actualPlaceName)
            assertEquals(targetStaccato.address, actualAddress)

            // 추억 선택을 위한 데이터 검사
            val expectedSelectableMemories =
                dummyMemoryCandidates.filterCandidatesBy(targetStaccato.visitedAt.toLocalDate())
            val actualSelectableMemories = viewModel.selectableMemories.getOrAwaitValue()
            val actualMemoryCandidates = viewModel.memoryCandidates.getOrAwaitValue()
            val actualSelectedMemory = viewModel.selectedMemory.getOrAwaitValue()
            assertEquals(expectedSelectableMemories, actualSelectableMemories)
            assertEquals(dummyMemoryCandidates, actualMemoryCandidates)
            assertEquals(targetMemoryCandidate, actualSelectedMemory)

            // 일시 선택을 위한 데이터 검사
            val actualSelectedVisitedAt = viewModel.selectedVisitedAt.getOrAwaitValue()
            assertEquals(targetStaccato.visitedAt, actualSelectedVisitedAt)
        }

    @Test
    fun `일시가 바뀌면 그에 따라 선택 가능 카테고리, 선택된 카테고리를 업데이트 한다`() =
        runTest {
            // given : 뷰모델을 초기화 한다
            givenTargetStaccatoWithRepositorySetup()

            viewModel.fetchTargetData(staccatoId = TARGET_STACCATO_ID)
            advanceUntilIdle()

            // when : 현재 선택된 카테고리 범위 밖의 날짜로 바뀌면
            val newLocalDate = yearEnd2023.atStartOfDay() // 수정 후 방문 날짜
            viewModel.setMemoryCandidateBy(newLocalDate)

            // then : 바뀐 날짜 기준으로 유효한 값을 업데이트한다
            val expectedMemories = listOf(newMemoryCandidate)
            val actualMemories = viewModel.selectableMemories.getOrAwaitValue()

            val expectedMemory = newMemoryCandidate
            val actualMemory = viewModel.selectedMemory.getOrAwaitValue()

            assertEquals(expectedMemories, actualMemories)
            assertEquals(expectedMemory, actualMemory)
        }

    private fun givenTargetStaccatoWithRepositorySetup(): Staccato {
        val targetStaccato =
            makeTestStaccato(
                staccatoId = TARGET_STACCATO_ID,
                memoryCandidate = targetMemoryCandidate,
                visitedAt = yearEnd2024.atTime(12, 0),
            )
        setupRepositoriesWithDummyData(targetStaccato)
        return targetStaccato
    }

    private fun setupRepositoriesWithDummyData(targetStaccato: Staccato) {
        coEvery { timelineRepository.getMemoryCandidates() } returns
            ResponseResult.Success(
                dummyMemoryCandidates,
            )
        coEvery { staccatoRepository.getStaccato(TARGET_STACCATO_ID) } returns
            ResponseResult.Success(targetStaccato)
    }
}
