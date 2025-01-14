package com.on.staccato.data.category

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.memory.CategoryCreationResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.NewMemory
import com.on.staccato.domain.repository.MemoryRepository
import javax.inject.Inject

class CategoryDefaultRepository
    @Inject
    constructor(
        private val categoryDataSource: CategoryDataSource,
    ) : MemoryRepository {
        override suspend fun getMemory(categoryId: Long): ResponseResult<Memory> {
            return when (val responseResult = categoryDataSource.getCategory(categoryId)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
            }
        }

        override suspend fun getMemories(currentDate: String?): ResponseResult<MemoryCandidates> {
            return when (val responseResult = categoryDataSource.getCategories(currentDate)) {
                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )
            }
        }

        override suspend fun createMemory(newCategory: NewMemory): ResponseResult<CategoryCreationResponse> {
            return when (val responseResult = categoryDataSource.createCategory(newCategory)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        override suspend fun updateMemory(
            categoryId: Long,
            newCategory: NewMemory,
        ): ResponseResult<Unit> {
            return when (val responseResult = categoryDataSource.updateCategory(categoryId, newCategory)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        override suspend fun deleteMemory(categoryId: Long): ResponseResult<Unit> {
            return when (val responseResult = categoryDataSource.deleteCategory(categoryId)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(Unit)
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
