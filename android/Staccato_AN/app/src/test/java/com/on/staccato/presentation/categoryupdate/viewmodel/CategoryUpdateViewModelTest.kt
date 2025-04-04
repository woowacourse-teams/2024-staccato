package com.on.staccato.presentation.categoryupdate.viewmodel

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.InstantTaskExecutorExtension
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.category.VALID_ID
import com.on.staccato.presentation.category.category
import com.on.staccato.presentation.categoryupdate.CategoryUpdateError
import com.on.staccato.presentation.getOrAwaitValue
import com.on.staccato.presentation.util.ExceptionState2
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CategoryUpdateViewModelTest {
    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @MockK
    private lateinit var imageRepository: ImageRepository

    @InjectMockKs
    private lateinit var viewModel: CategoryUpdateViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `수정하려는 카테고리를 가져온다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)

            // when
            viewModel.fetchCategory(VALID_ID)

            // then
            val actual = viewModel.title.getOrAwaitValue()
            assertThat(actual).isEqualTo("카테고리 제목")
        }

    @Test
    fun `수정하려는 카테고리를 가져오는 중 서버 오류가 발생하면 에러 메시지를 설정한다 `() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns ServerError(Status.Code(400), "Bad Request")

            // when
            viewModel.fetchCategory(VALID_ID)

            // then
            val actual = viewModel.errorMessage.getOrAwaitValue()
            assertThat(actual).isEqualTo("Bad Request")
        }

    @Test
    fun `수정하려는 카테고리를 가져오는 중 네트워크 오류가 발생하면 예외를 설정한다 `() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Exception.NetworkError()

            // when
            viewModel.fetchCategory(VALID_ID)

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryUpdateError.CategoryInitialization(ExceptionState2.NetworkError)::class.java)
        }

    @Test
    fun `수정하려는 카테고리를 가져오는 중 알 수 없는 오류가 발생하면 예외를 설정한다 `() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Exception.UnknownError()

            // when
            viewModel.fetchCategory(VALID_ID)

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryUpdateError.CategoryInitialization(ExceptionState2.UnknownError)::class.java)
        }

    @Test
    fun `카테고리를 수정에 성공하면 수정된 카테고리의 ID를 가져온다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.fetchCategory(VALID_ID)

            viewModel.title.value = "카테고리 제목 수정"
            viewModel.description.value = "카테고리 내용"
            coEvery {
                categoryRepository.updateCategory(
                    VALID_ID,
                    getNewCategory(),
                )
            } returns Success(Unit)

            // when
            viewModel.updateCategory()

            // then
            val actual = viewModel.isUpdateSuccess.getOrAwaitValue()
            assertThat(actual).isTrue()
        }

    @Test
    fun `카테고리 수정 중 서버 오류가 발생하면 에러 메시지를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.fetchCategory(VALID_ID)

            viewModel.title.value = "카테고리 제목 수정"
            viewModel.description.value = "카테고리 내용"
            coEvery {
                categoryRepository.updateCategory(
                    VALID_ID,
                    getNewCategory(),
                )
            } returns ServerError(Status.Code(400), "Bad Request")

            // when
            viewModel.updateCategory()

            // then
            val actual = viewModel.errorMessage.getOrAwaitValue()
            assertThat(actual).isEqualTo("Bad Request")
        }

    @Test
    fun `카테고리 수정 중 네트워크 오류가 발생하면 예외를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.fetchCategory(VALID_ID)

            viewModel.title.value = "카테고리 제목 수정"
            viewModel.description.value = "카테고리 내용"
            coEvery {
                categoryRepository.updateCategory(
                    VALID_ID,
                    getNewCategory(),
                )
            } returns Exception.NetworkError()

            // when
            viewModel.updateCategory()

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryUpdateError.CategoryUpdate(ExceptionState2.NetworkError)::class.java)
        }

    @Test
    fun `카테고리 수정 중 알 수 없는 오류가 발생하면 예외를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.fetchCategory(VALID_ID)

            viewModel.title.value = "카테고리 제목 수정"
            viewModel.description.value = "카테고리 내용"
            coEvery {
                categoryRepository.updateCategory(
                    VALID_ID,
                    getNewCategory(),
                )
            } returns Exception.UnknownError()

            // when
            viewModel.updateCategory()

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryUpdateError.CategoryUpdate(ExceptionState2.UnknownError)::class.java)
        }

    @Test
    fun `카테고리 썸네일 이미지를 초기화하면 기존 uri, url 값을 제거한다`() {
        // when
        viewModel.clearThumbnail()

        // then
        val actual = viewModel.thumbnail.value

        assertAll(
            { assertThat(actual?.uri).isNull() },
            { assertThat(actual?.url).isNull() },
        )
    }

    @Test
    fun `카테고리 기간 설정 시 시작일과 종료일이 올바르게 변환된다`() {
        // when
        viewModel.setCategoryPeriod(startAt = 1743798000000, endAt = 1743967200000)

        // then
        val startDate = viewModel.startDate.getOrAwaitValue()
        val endDate = viewModel.endDate.getOrAwaitValue()

        assertAll(
            { assertThat(startDate).isEqualTo(LocalDate.of(2025, 4, 5)) },
            { assertThat(endDate).isEqualTo(LocalDate.of(2025, 4, 7)) },
        )
    }

    private fun getNewCategory() =
        NewCategory(
            categoryThumbnailUrl = null,
            categoryTitle = viewModel.title.getOrAwaitValue(),
            startAt = null,
            endAt = null,
            description = viewModel.description.getOrAwaitValue(),
        )
}
