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
        override suspend fun getMemory(categoryId: Long): ResponseResult<MemoryResponse> =
            handleApiResponse { memoryApiService.getCategory(categoryId) }

        override suspend fun getMemories(currentDate: String?): ResponseResult<MemoriesResponse> =
            handleApiResponse { memoryApiService.getCategories(currentDate) }

        override suspend fun createMemory(newCategory: NewMemory): ResponseResult<MemoryCreationResponse> =
            handleApiResponse {
                memoryApiService.postCategory(newCategory.toDto())
            }

        override suspend fun updateMemory(
            categoryId: Long,
            newCategory: NewMemory,
        ): ResponseResult<Unit> =
            handleApiResponse {
                memoryApiService.putCategory(categoryId, newCategory.toDto())
            }

        override suspend fun deleteMemory(categoryId: Long): ResponseResult<Unit> =
            handleApiResponse {
                memoryApiService.deleteCategory(
                    categoryId,
                )
            }
    }
