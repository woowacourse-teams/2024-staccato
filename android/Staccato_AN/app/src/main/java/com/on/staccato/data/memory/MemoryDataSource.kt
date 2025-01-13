package com.on.staccato.data.memory

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

interface MemoryDataSource {
    suspend fun getMemory(categoryId: Long): ResponseResult<MemoryResponse>

    suspend fun getMemories(currentDate: String?): ResponseResult<MemoriesResponse>

    suspend fun createMemory(newCategory: NewMemory): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        categoryId: Long,
        newCategory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(categoryId: Long): ResponseResult<Unit>
}
