package com.on.staccato.data.category

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.dto.memory.CategoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory
import javax.inject.Inject

class CategoryRemoteDataSource
    @Inject
    constructor(
        private val categoryApiService: CategoryApiService,
    ) : CategoryDataSource {
        override suspend fun getCategory(categoryId: Long): ResponseResult<MemoryResponse> =
            handleApiResponse { categoryApiService.getCategory(categoryId) }

        override suspend fun getCategories(currentDate: String?): ResponseResult<CategoriesResponse> =
            handleApiResponse { categoryApiService.getCategories(currentDate) }

        override suspend fun createCategory(newCategory: NewMemory): ResponseResult<MemoryCreationResponse> =
            handleApiResponse {
                categoryApiService.postCategory(newCategory.toDto())
            }

        override suspend fun updateCategory(
            categoryId: Long,
            newCategory: NewMemory,
        ): ResponseResult<Unit> =
            handleApiResponse {
                categoryApiService.putCategory(categoryId, newCategory.toDto())
            }

        override suspend fun deleteCategory(categoryId: Long): ResponseResult<Unit> =
            handleApiResponse {
                categoryApiService.deleteCategory(
                    categoryId,
                )
            }
    }
