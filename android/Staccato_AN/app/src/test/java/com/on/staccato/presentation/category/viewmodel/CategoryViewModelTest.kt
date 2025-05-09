package com.on.staccato.presentation.category.viewmodel

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.InstantTaskExecutorExtension
import com.on.staccato.data.dto.Status
import com.on.staccato.data.network.Exception
import com.on.staccato.data.network.ServerError
import com.on.staccato.data.network.Success
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.presentation.category.INVALID_ID
import com.on.staccato.presentation.category.VALID_ID
import com.on.staccato.presentation.category.category
import com.on.staccato.presentation.category.categoryUiModel
import com.on.staccato.presentation.getOrAwaitValue
import com.on.staccato.presentation.util.ExceptionState2
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CategoryViewModelTest {
    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @InjectMockKs
    private lateinit var viewModel: CategoryViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `조회하려는 카테고리 ID가 유효하지 않으면 예외 상태를 설정한다`() {
        // when
        viewModel.loadCategory(INVALID_ID)

        // then
        val actual = viewModel.exceptionState.getOrAwaitValue()
        assertThat(actual).isInstanceOf(ExceptionState2.UnknownError::class.java)
    }

    @Test
    fun `조회하려는 카테고리 ID가 유효하면 카테고리를 가져온다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)

            // when
            viewModel.loadCategory(VALID_ID)

            // then
            val actual = viewModel.category.getOrAwaitValue()
            assertThat(actual).isEqualTo(categoryUiModel)
        }

    @Test
    fun `카테고리 조회 중 서버 오류가 발생하면 에러 메시지를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns
                ServerError(
                    Status.Code(400),
                    "Bad Request",
                )

            // when
            viewModel.loadCategory(VALID_ID)

            // then
            val actual = viewModel.errorMessage.getOrAwaitValue()
            assertThat(actual).isEqualTo("Bad Request")
        }

    @Test
    fun `카테고리 조회 중 네트워크 오류가 발생하면 예외 상태를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Exception.NetworkError()

            // when
            viewModel.loadCategory(VALID_ID)

            // then
            val actual = viewModel.exceptionState.getOrAwaitValue()
            assertThat(actual).isInstanceOf(ExceptionState2.NetworkError::class.java)
        }

    @Test
    fun `카테고리 조회 중 알 수 없는 오류가 발생하면 예외 상태를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Exception.UnknownError()

            // when
            viewModel.loadCategory(VALID_ID)

            // then
            val actual = viewModel.exceptionState.getOrAwaitValue()
            assertThat(actual).isInstanceOf(ExceptionState2.UnknownError::class.java)
        }

    @Test
    fun `카테고리가 삭제되면 삭제 성공 상태를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.loadCategory(VALID_ID)
            coEvery { categoryRepository.deleteCategory(VALID_ID) } returns Success(Unit)

            // when
            viewModel.deleteCategory()

            // then
            val actual = viewModel.isDeleted.getOrAwaitValue()
            assertThat(actual).isTrue()
        }

    @Test
    fun `카테고리 삭제 중 서버 오류가 발생하면 에러 메시지를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.loadCategory(VALID_ID)
            coEvery { categoryRepository.deleteCategory(VALID_ID) } returns
                ServerError(
                    Status.Code(400),
                    "Bad Request",
                )

            // when
            viewModel.deleteCategory()

            // then
            val errorMessage = viewModel.errorMessage.getOrAwaitValue()
            assertThat(errorMessage).isEqualTo("Bad Request")
        }

    @Test
    fun `카테고리 삭제 중 네트워크 오류가 발생하면 예외 상태를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.loadCategory(VALID_ID)
            coEvery { categoryRepository.deleteCategory(VALID_ID) } returns Exception.NetworkError()

            // when
            viewModel.deleteCategory()

            // then
            val exceptionState = viewModel.exceptionState.getOrAwaitValue()
            assertThat(exceptionState).isInstanceOf(ExceptionState2.NetworkError::class.java)
        }

    @Test
    fun `카테고리 삭제 중 알 수 없는 오류가 발생하면 예외 상태를 설정한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns Success(category)
            viewModel.loadCategory(VALID_ID)
            coEvery { categoryRepository.deleteCategory(VALID_ID) } returns Exception.UnknownError()

            // when
            viewModel.deleteCategory()

            // then
            val exceptionState = viewModel.exceptionState.getOrAwaitValue()
            assertThat(exceptionState).isInstanceOf(ExceptionState2.UnknownError::class.java)
        }
}
