package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory

interface MemoryRepository {
    suspend fun getMemory(categoryId: Long): ResponseResult<Category>

    suspend fun getMemories(currentDate: String?): ResponseResult<CategoryCandidates>

    suspend fun createMemory(newCategory: NewCategory): ResponseResult<CategoryCreationResponse>

    suspend fun updateMemory(
        categoryId: Long,
        newCategory: NewCategory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(categoryId: Long): ResponseResult<Unit>
}
