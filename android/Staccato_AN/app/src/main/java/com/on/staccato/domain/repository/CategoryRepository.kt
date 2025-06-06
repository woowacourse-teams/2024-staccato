package com.on.staccato.domain.repository

import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.model.Timeline

interface CategoryRepository {
    suspend fun getCategory(categoryId: Long): ApiResult<Category>

    suspend fun changeCategoryColor(
        categoryId: Long,
        color: String,
    ): ApiResult<Unit>

    suspend fun getCategories(
        sort: String?,
        filter: String?,
    ): ApiResult<Timeline>

    suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates>

    // TODO: 현재 사용 되지 않음
    suspend fun getCategoriesBy(currentDate: String?): ApiResult<CategoryCandidates>

    suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewCategory,
    ): ApiResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ApiResult<Unit>
}
