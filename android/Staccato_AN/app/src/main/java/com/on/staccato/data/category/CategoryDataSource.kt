package com.on.staccato.data.category

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.memory.CategoriesResponse
import com.on.staccato.data.dto.memory.CategoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.NewMemory

interface CategoryDataSource {
    suspend fun getCategory(categoryId: Long): ResponseResult<MemoryResponse>

    suspend fun getCategories(currentDate: String?): ResponseResult<CategoriesResponse>

    suspend fun createCategory(newCategory: NewMemory): ResponseResult<CategoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ResponseResult<Unit>
}
