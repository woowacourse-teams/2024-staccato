package com.on.staccato.data.memory

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory
import javax.inject.Inject

class MemoryRemoteDataSource
    @Inject
    constructor(
        private val memoryApiService: MemoryApiService,
    ) : MemoryDataSource {
        override suspend fun getMemory(memoryId: Long): ApiResult<MemoryResponse> = memoryApiService.getMemory(memoryId)

        override suspend fun getMemories(currentDate: String?): ApiResult<MemoriesResponse> = memoryApiService.getMemories(currentDate)

        override suspend fun createMemory(newMemory: NewMemory): ApiResult<MemoryCreationResponse> =
            memoryApiService.postMemory(
                newMemory.toDto(),
            )

        override suspend fun updateMemory(
            memoryId: Long,
            newMemory: NewMemory,
        ): ApiResult<Unit> = memoryApiService.putMemory(memoryId, newMemory.toDto())

        override suspend fun deleteMemory(memoryId: Long): ApiResult<Unit> =
            memoryApiService.deleteMemory(
                memoryId,
            )
    }
