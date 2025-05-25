package com.on.staccato.data.category

import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.mapper.toCategoryCandidates
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryDefaultRepository
    @Inject
    constructor(
        private val categoryDataSource: CategoryDataSource,
    ) : CategoryRepository {
        override suspend fun getCategory(categoryId: Long): ApiResult<Category> =
            categoryDataSource.getCategory(categoryId).handle { it.toDomain() }

        override suspend fun changeCategoryColor(
            categoryId: Long,
            color: String,
        ): ApiResult<Unit> = categoryDataSource.changeCategoryColor(categoryId, color)

        override suspend fun getCategories(
            sort: String?,
            filter: String?,
        ): ApiResult<Timeline> =
            categoryDataSource.getCategories(sort, filter)
                .handle { it.toDomain() }

        override suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates> =
            categoryDataSource.getCategories().handle {
                it.toCategoryCandidates()
            }

        // TODO: 현재 사용 되지 않음
        override suspend fun getCategoriesBy(currentDate: String?): ApiResult<CategoryCandidates> =
            categoryDataSource.getCategoriesBy(currentDate).handle { it.toDomain() }

        override suspend fun createCategory(newCategory: NewCategory): ApiResult<CategoryCreationResponse> =
            categoryDataSource.createCategory(newCategory).handle { it }

        override suspend fun updateCategory(
            categoryId: Long,
            newCategory: NewCategory,
        ): ApiResult<Unit> = categoryDataSource.updateCategory(categoryId, newCategory).handle()

        override suspend fun deleteCategory(categoryId: Long): ApiResult<Unit> = categoryDataSource.deleteCategory(categoryId).handle()
    }
