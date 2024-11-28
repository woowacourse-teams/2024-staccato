package com.on.staccato.data.memory

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
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
        override suspend fun getMemory(memoryId: Long): ApiResult<Memory> {
            return when (val responseResult = memoryDataSource.getMemory(memoryId)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data.toDomain())
            }
        }

        override suspend fun getMemories(currentDate: String?): ApiResult<MemoryCandidates> {
            return when (val responseResult = memoryDataSource.getMemories(currentDate)) {
                is Success -> Success(responseResult.data.toDomain())
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
            }
        }

        override suspend fun createMemory(newMemory: NewMemory): ApiResult<MemoryCreationResponse> {
            return when (val responseResult = memoryDataSource.createMemory(newMemory)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun updateMemory(
            memoryId: Long,
            newMemory: NewMemory,
        ): ApiResult<Unit> {
            return when (val responseResult = memoryDataSource.updateMemory(memoryId, newMemory)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun deleteMemory(memoryId: Long): ApiResult<Unit> {
            return when (val responseResult = memoryDataSource.deleteMemory(memoryId)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(Unit)
            }
        }
    }
