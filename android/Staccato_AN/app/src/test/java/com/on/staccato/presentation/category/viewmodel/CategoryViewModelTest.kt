package com.on.staccato.presentation.category.viewmodel

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.InstantTaskExecutorExtension
import com.on.staccato.data.dto.Status
import com.on.staccato.data.network.Exception
import com.on.staccato.data.network.ServerError
import com.on.staccato.data.network.Success
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.category.INVALID_ID
import com.on.staccato.presentation.category.VALID_ID
import com.on.staccato.presentation.category.category
import com.on.staccato.presentation.category.categoryUiModel
import com.on.staccato.presentation.category.naMembers
import com.on.staccato.presentation.category.naMembersUiModel
import com.on.staccato.presentation.category.nana
import com.on.staccato.presentation.category.participants
import com.on.staccato.presentation.category.selectedNaMembersUiModel
import com.on.staccato.presentation.getOrAwaitValue
import com.on.staccato.presentation.util.ExceptionState2
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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

    @MockK
    private lateinit var memberRepository: MemberRepository

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

    @Test
    fun `빙티, 해나, 호두가 참여한 카테고리에서 '나'로 검색하면 해나(참여중)와 나나(선택가능)를 반환한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns
                Success(
                    category.copy(
                        mates = participants,
                    ),
                )
            coEvery { memberRepository.searchMembersBy("나") } returns MutableStateFlow(Success(naMembers))
            viewModel.loadCategory(VALID_ID)

            // when
            viewModel.searchMembersBy("나")

            // then
            val result = viewModel.members.first()
            assertThat(result).isEqualTo(naMembersUiModel)
        }

    @Test
    fun `'나'로 검색한 뒤 나나를 선택하면 해나(참여중)와 나나(선택됨)를 반환한다`() =
        runTest {
            // given
            coEvery { categoryRepository.getCategory(VALID_ID) } returns
                Success(
                    category.copy(
                        mates = participants,
                    ),
                )
            coEvery { memberRepository.searchMembersBy("나") } returns MutableStateFlow(Success(naMembers))
            viewModel.loadCategory(VALID_ID)
            viewModel.searchMembersBy("나")

            // when
            viewModel.select(nana)

            // then
            val result = viewModel.members.first()
            assertThat(result).isEqualTo(selectedNaMembersUiModel)
        }
}
