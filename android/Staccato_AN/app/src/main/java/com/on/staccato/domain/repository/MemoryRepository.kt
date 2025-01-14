package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.memory.CategoryCreationResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.NewMemory

interface MemoryRepository {
    suspend fun getMemory(memoryId: Long): ResponseResult<Memory>

    suspend fun getMemories(currentDate: String?): ResponseResult<MemoryCandidates>

    suspend fun createMemory(newMemory: NewMemory): ResponseResult<CategoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
