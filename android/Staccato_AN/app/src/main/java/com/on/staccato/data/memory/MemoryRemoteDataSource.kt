package com.on.staccato.data.memory

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory
import javax.inject.Inject

class MemoryRemoteDataSource
    @Inject
    constructor(
        private val memoryApiService: CategoryApiService,
    ) : MemoryDataSource {
        override suspend fun getCategory(categoryId: Long): ResponseResult<MemoryResponse> =
            handleApiResponse { memoryApiService.getCategory(categoryId) }

        override suspend fun getCategories(currentDate: String?): ResponseResult<MemoriesResponse> =
            handleApiResponse { memoryApiService.getCategories(currentDate) }

        override suspend fun createCategory(newCategory: NewMemory): ResponseResult<MemoryCreationResponse> =
            handleApiResponse {
                memoryApiService.postCategory(newCategory.toDto())
            }

        override suspend fun updateCategory(
            categoryId: Long,
            newCategory: NewMemory,
        ): ResponseResult<Unit> =
            handleApiResponse {
                memoryApiService.putCategory(categoryId, newCategory.toDto())
            }

        override suspend fun deleteCategory(categoryId: Long): ResponseResult<Unit> =
            handleApiResponse {
                memoryApiService.deleteCategory(
                    categoryId,
                )
            }
    }
