package com.on.staccato.data.memory

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.NewMemory
import com.on.staccato.domain.repository.MemoryRepository
import javax.inject.Inject

class MemoryDefaultRepository
    @Inject
    constructor(
        private val memoryDataSource: MemoryDataSource,
    ) : MemoryRepository {
        override suspend fun getMemory(memoryId: Long): ResponseResult<Memory> {
            return when (val responseResult = memoryDataSource.getMemory(memoryId)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
            }
        }

        override suspend fun getMemories(currentDate: String?): ResponseResult<MemoryCandidates> {
            return when (val responseResult = memoryDataSource.getMemories(currentDate)) {
                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )
            }
        }

        override suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse> {
            return when (val responseResult = memoryDataSource.createMemory(newMemory)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        override suspend fun updateMemory(
            memoryId: Long,
            newMemory: NewMemory,
        ): ResponseResult<Unit> {
            return when (val responseResult = memoryDataSource.updateMemory(memoryId, newMemory)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        override suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit> {
            return when (val responseResult = memoryDataSource.deleteMemory(memoryId)) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(Unit)
            }
        }

        companion object {
            const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
        }
    }
