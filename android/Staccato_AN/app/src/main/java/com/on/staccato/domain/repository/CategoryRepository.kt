package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory

interface CategoryRepository {
    suspend fun getCategory(categoryId: Long): ApiResult<Category>

    suspend fun getCategories(currentDate: String?): ApiResult<CategoryCandidates>

    suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewCategory,
    ): ApiResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ApiResult<Unit>
}
