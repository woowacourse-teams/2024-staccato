package com.on.staccato.data.memory

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

interface MemoryDataSource {
    suspend fun getMemory(memoryId: Long): ApiResult<MemoryResponse>

    suspend fun getMemories(currentDate: String?): ApiResult<MemoriesResponse>

    suspend fun createMemory(newMemory: NewMemory): ApiResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ApiResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ApiResult<Unit>
}
