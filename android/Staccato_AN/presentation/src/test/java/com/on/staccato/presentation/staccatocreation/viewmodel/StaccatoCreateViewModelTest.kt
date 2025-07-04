package com.on.staccato.presentation.staccatocreation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.on.staccato.domain.Success
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.TARGET_CATEGORY_ID
import com.on.staccato.domain.model.categoryCandidateWithId1
import com.on.staccato.domain.model.dummyCategoryCandidates
import com.on.staccato.domain.model.endDateOf2023
import com.on.staccato.domain.model.middleDateOf2024
import com.on.staccato.domain.model.startDateOf2024
import com.on.staccato.domain.model.targetCategoryCandidate
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.MainDispatcherRule
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
class StaccatoCreateViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @MockK
    private lateinit var staccatoRepository: StaccatoRepository

    @MockK
    private lateinit var imageRepository: ImageRepository

    @MockK
    private lateinit var locationRepository: LocationRepository

    @InjectMockKs
    private lateinit var viewModel: StaccatoCreationViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        givenCategoryCandidatesReturnsSuccessWithDummyData()
    }

    @Test
    fun `viewModel 초기화 시 카테고리 후보를 불러온다`() =
        runTest {
            // when
            viewModel.fetchAllCategories()
            advanceUntilIdle()

            // then
            val actualCategoryCandidates = viewModel.allCategories.value
            assertEquals(dummyCategoryCandidates, actualCategoryCandidates)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 현재 날짜에서 선택 가능한 카테고리 후보 중 첫번째를 선택한다`() =
        runTest {
            // given
            viewModel.fetchAllCategories()
            advanceUntilIdle()

            // when
            val currentLocalDate = middleDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(0L, currentLocalDate)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.value
            val actualSelectableCategories = viewModel.selectableCategories.value
            val actualSelectedCategory = viewModel.selectedCategory.value

            val selectableCategories = dummyCategoryCandidates.filterBy(middleDateOf2024)
            val selectedCategory = selectableCategories.findByIdOrFirst(null)

            assertEquals(currentLocalDate, actualVisitedAt)
            assertEquals(selectableCategories, actualSelectableCategories)
            assertEquals(selectedCategory, actualSelectedCategory)
        }

    @Test
    fun `카테고리 ID가 0L이 아닐 때는 id로 카테고리를 선택하고, 현재와 가장 가까운 일시를 선택한다`() =
        runTest {
            // given
            viewModel.fetchAllCategories()
            advanceUntilIdle()

            // when
            val currentVisitedAt = middleDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(TARGET_CATEGORY_ID, currentVisitedAt)

            // then
            val actualVisitedAt = viewModel.selectedVisitedAt.value
            val actualSelectableCategories = viewModel.selectableCategories.value
            val actualSelectedCategory = viewModel.selectedCategory.value

            val closestVisitedAt = targetCategoryCandidate.getClosestDateTime(currentVisitedAt)
            val fixedSelectableCategories = CategoryCandidates.from(targetCategoryCandidate)
            val fixedSelectedCategory = targetCategoryCandidate

            assertEquals(closestVisitedAt, actualVisitedAt)
            assertEquals(fixedSelectableCategories, actualSelectableCategories)
            assertEquals(fixedSelectedCategory, actualSelectedCategory)
        }

    @Test
    fun `카테고리 ID가 0L일 때는 일시가 바뀌면 categoryCandidate도 바뀐다`() =
        runTest {
            // given
            viewModel.fetchAllCategories()

            val oldLocalDate = startDateOf2024.atStartOfDay()
            viewModel.initCategoryAndVisitedAt(TARGET_CATEGORY_ID, oldLocalDate)

            // when
            val newLocalDate = endDateOf2023.atStartOfDay()
            viewModel.updateCategorySelectionBy(newLocalDate)

            // then
            val expectedSelectableCategories = CategoryCandidates.from(categoryCandidateWithId1)
            val expectedSelectedCategory = categoryCandidateWithId1

            val actualSelectableCategories = viewModel.selectableCategories.value
            val actualSelectedCategory = viewModel.selectedCategory.value

            assertEquals(expectedSelectableCategories, actualSelectableCategories)
            assertEquals(expectedSelectedCategory, actualSelectedCategory)
        }

    private fun givenCategoryCandidatesReturnsSuccessWithDummyData() {
        coEvery { categoryRepository.getCategoryCandidates() } returns
            Success(
                dummyCategoryCandidates,
            )
    }
}
