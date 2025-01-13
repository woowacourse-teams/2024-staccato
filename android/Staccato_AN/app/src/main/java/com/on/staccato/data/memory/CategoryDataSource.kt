package com.on.staccato.data.memory

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

interface CategoryDataSource {
    suspend fun getCategory(categoryId: Long): ResponseResult<MemoryResponse>

    suspend fun getCategories(currentDate: String?): ResponseResult<MemoriesResponse>

    suspend fun createCategory(newCategory: NewMemory): ResponseResult<MemoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ResponseResult<Unit>
}
