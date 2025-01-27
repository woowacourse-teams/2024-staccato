package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.NewMemory

interface MemoryRepository {
    suspend fun getMemory(memoryId: Long): ApiResult<Memory>

    suspend fun getMemories(currentDate: String?): ApiResult<MemoryCandidates>

    suspend fun createMemory(newMemory: NewMemory): ApiResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ApiResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ApiResult<Unit>
}
