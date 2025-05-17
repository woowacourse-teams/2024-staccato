package com.on.staccato.data.category

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryResponse
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.NewCategory

interface CategoryDataSource {
    suspend fun getCategory(categoryId: Long): ApiResult<CategoryResponse>

    suspend fun changeCategoryColor(
        categoryId: Long,
        color: String,
    ): ApiResult<Unit>

    suspend fun getCategories(
        sort: String? = null,
        filter: String? = null,
    ): ApiResult<TimelineResponse>

    suspend fun getCategoriesBy(currentDate: String?): ApiResult<CategoriesResponse>

    suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse>

    suspend fun updateCategory(
        categoryId: Long,
        newCategory: NewCategory,
    ): ApiResult<Unit>

    suspend fun deleteCategory(categoryId: Long): ApiResult<Unit>
}
