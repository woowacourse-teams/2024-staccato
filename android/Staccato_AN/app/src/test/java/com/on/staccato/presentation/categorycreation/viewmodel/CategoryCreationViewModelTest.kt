package com.on.staccato.presentation.categorycreation.viewmodel

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.InstantTaskExecutorExtension
import com.on.staccato.data.dto.Status
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.network.Exception
import com.on.staccato.data.network.ServerError
import com.on.staccato.data.network.Success
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.categorycreation.model.CategoryCreationError
import com.on.staccato.presentation.categorycreation.newCategory
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
import java.util.Locale
import java.util.TimeZone

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CategoryCreationViewModelTest {
    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @MockK
    private lateinit var imageRepository: ImageRepository

    @InjectMockKs
    private lateinit var viewModel: CategoryCreationViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel.title.value = "카테고리 제목"
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        Locale.setDefault(Locale.KOREA)
    }

    @Test
    fun `카테고리를 생성하면 생성된 카테고리의 ID를 가져온다`() =
        runTest {
            // given
            coEvery {
                categoryRepository.createCategory(newCategory.copy(categoryTitle = viewModel.title.getOrAwaitValue()))
            } returns
                Success(
                    CategoryCreationResponse(categoryId = 1L),
                )

            // when
            viewModel.createCategory()

            // then
            val createdCategoryId = viewModel.createdCategoryId.getOrAwaitValue()
            assertThat(createdCategoryId).isEqualTo(1L)
        }

    @Test
    fun `카테고리 생성 중 서버 오류가 발생하면 에러 메시지를 설정한다`() =
        runTest {
            // given
            coEvery {
                categoryRepository.createCategory(newCategory.copy(categoryTitle = viewModel.title.getOrAwaitValue()))
            } returns ServerError(Status.Code(400), "Bad Request")

            // when
            viewModel.createCategory()

            // then
            val actual = viewModel.errorMessage.getOrAwaitValue()
            assertThat(actual).isEqualTo("Bad Request")
        }

    @Test
    fun `카테고리 생성 중 네트워크 오류가 발생하면 예외를 설정한다`() =
        runTest {
            // given
            coEvery {
                categoryRepository.createCategory(newCategory.copy(categoryTitle = viewModel.title.getOrAwaitValue()))
            } returns Exception.NetworkError()

            // when
            viewModel.createCategory()

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryCreationError.CategoryCreation(ExceptionState2.NetworkError)::class.java)
        }

    @Test
    fun `카테고리 생성 중 알 수 없는 오류가 발생하면 예외를 설정한다`() =
        runTest {
            // given
            coEvery {
                categoryRepository.createCategory(newCategory.copy(categoryTitle = viewModel.title.getOrAwaitValue()))
            } returns Exception.UnknownError()

            // when
            viewModel.createCategory()

            // then
            val actual = viewModel.error.getOrAwaitValue()
            assertThat(actual).isInstanceOf(CategoryCreationError.CategoryCreation(ExceptionState2.UnknownError)::class.java)
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
        // startAt = 1743798000000은 2025년 4월 5일, endAt = 1743967200000은 2025년 4월 7일
        viewModel.setCategoryPeriod(startAt = 1743798000000, endAt = 1743967200000)

        // then
        val startDate = viewModel.startDate.getOrAwaitValue()
        val endDate = viewModel.endDate.getOrAwaitValue()

        assertAll(
            { assertThat(startDate).isEqualTo(LocalDate.of(2025, 4, 5)) },
            { assertThat(endDate).isEqualTo(LocalDate.of(2025, 4, 7)) },
        )
    }
}
