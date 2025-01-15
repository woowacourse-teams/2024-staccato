package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory

interface CategoryRepository {
    suspend fun getCategory(categoryId: Long): ResponseResult<Category>

    suspend fun getCategories(currentDate: String?): ResponseResult<CategoryCandidates>

    suspend fun createCategory(newCategory: NewCategory): ResponseResult<CategoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewCategory,
    ): ResponseResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ResponseResult<Unit>
}
