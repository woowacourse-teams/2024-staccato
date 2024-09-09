package com.on.staccato.data.memory

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

class MemoryRemoteDataSource(
    private val memoryApiService: MemoryApiService,
) : MemoryDataSource {
    override suspend fun getMemory(memoryId: Long): ResponseResult<MemoryResponse> =
        handleApiResponse { memoryApiService.getMemory(memoryId) }

    override suspend fun getMemories(currentDate: String?): ResponseResult<MemoriesResponse> =
        handleApiResponse { memoryApiService.getMemories(currentDate) }

    override suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse> =
        handleApiResponse {
            memoryApiService.postMemory(newMemory.toDto())
        }

    override suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit> =
        handleApiResponse {
            memoryApiService.putMemory(memoryId, newMemory.toDto())
        }

    override suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit> = handleApiResponse { memoryApiService.deleteMemory(memoryId) }
}
