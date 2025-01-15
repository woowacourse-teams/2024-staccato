package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory

interface MemoryRepository {
    suspend fun getMemory(memoryId: Long): ResponseResult<Category>

    suspend fun getMemories(currentDate: String?): ResponseResult<CategoryCandidates>

    suspend fun createMemory(newMemory: NewCategory): ResponseResult<CategoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewCategory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
