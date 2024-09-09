package com.on.staccato.data.memory

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

interface MemoryDataSource {
    suspend fun getMemory(memoryId: Long): ResponseResult<MemoryResponse>

    suspend fun getMemories(currentDate: String?): ResponseResult<MemoriesResponse>

    suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
