package com.on.staccato.data.category

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryResponse
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.NewCategory
import javax.inject.Inject

class CategoryRemoteDataSource
    @Inject
    constructor(
        private val categoryApiService: CategoryApiService,
    ) : CategoryDataSource {
        override suspend fun getCategory(categoryId: Long): ApiResult<CategoryResponse> =
            categoryApiService.getCategory(
                categoryId,
            )

        override suspend fun getCategories(currentDate: String?): ApiResult<CategoriesResponse> =
            categoryApiService.getCategories(
                currentDate,
            )

        override suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse> =
            categoryApiService.postCategory(
                newCategory.toDto(),
            )

        override suspend fun updateCategory(
            categoryId: Long,
            newCategory: NewCategory,
        ): ApiResult<Unit> =
            categoryApiService.putCategory(
                categoryId,
                newCategory.toDto(),
            )

        override suspend fun deleteCategory(categoryId: Long): ApiResult<Unit> =
            categoryApiService.deleteCategory(
                categoryId,
            )
    }
