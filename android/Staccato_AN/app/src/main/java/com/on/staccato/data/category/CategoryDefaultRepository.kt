package com.on.staccato.data.category

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.handle
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryDefaultRepository
    @Inject
    constructor(
        private val categoryDataSource: CategoryDataSource,
    ) : CategoryRepository {
        override suspend fun getCategory(categoryId: Long): ApiResult<Category> = categoryDataSource.getCategory(categoryId).handle { it.toDomain() }

        override suspend fun getCategories(currentDate: String?): ApiResult<CategoryCandidates> =
            categoryDataSource.getCategories(currentDate).handle { it.toDomain() }

        override suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse> =
            categoryDataSource.createCategory(newCategory).handle { it }

        override suspend fun updateCategory(
            categoryId: Long,
            newCategory: NewCategory,
        ): ApiResult<Unit> = categoryDataSource.updateCategory(categoryId, newCategory).handle()

        override suspend fun deleteCategory(categoryId: Long): ApiResult<Unit> = categoryDataSource.deleteCategory(categoryId).handle()
    }
