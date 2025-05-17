package com.on.staccato.data

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.InstantTaskExecutorExtension
import com.on.staccato.data.category.CategoryApiService
import com.on.staccato.data.category.CategoryRemoteDataSource
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.network.Success
import com.on.staccato.presentation.categorycreation.newCategory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CategoryRemoteDataSourceTest {
    @MockK
    private lateinit var apiService: CategoryApiService

    @InjectMockKs
    private lateinit var dataSource: CategoryRemoteDataSource

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `공유 카테고리를 생성하면 공유가 활성화된 카테고리 요청이 성공적으로 수행된다`() =
        runTest {
            // given
            val categoryRequest = slot<CategoryRequest>()
            coEvery { apiService.postCategory(capture(categoryRequest)) } returns Success(CategoryCreationResponse(categoryId = 1L))

            // when
            val sharedCategory = newCategory.copy(categoryTitle = "공유 카테고리", isShared = true)
            val actual = dataSource.createCategory(newCategory = sharedCategory)

            // then
            assertAll(
                { assertThat(categoryRequest.captured.isShared).isTrue() },
                { assertThat(actual).isInstanceOf(Success::class.java) },
            )
        }

    @Test
    fun `개인 카테고리를 생성하면 공유가 비활성화된 카테고리 요청이 성공적으로 수행된다`() =
        runTest {
            // given
            val categoryRequest = slot<CategoryRequest>()
            coEvery { apiService.postCategory(capture(categoryRequest)) } returns Success(CategoryCreationResponse(categoryId = 1L))

            // when
            val personalCategory = newCategory.copy(categoryTitle = "개인 카테고리", isShared = false)
            val actual = dataSource.createCategory(newCategory = personalCategory)

            // then
            assertAll(
                { assertThat(categoryRequest.captured.isShared).isFalse() },
                { assertThat(actual).isInstanceOf(Success::class.java) },
            )
        }
}
