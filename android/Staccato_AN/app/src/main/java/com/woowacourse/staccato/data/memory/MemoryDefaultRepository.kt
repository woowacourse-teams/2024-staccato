package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.NewMemory
import com.woowacourse.staccato.domain.repository.MemoryRepository

class MemoryDefaultRepository(
    private val memoryDataSource: MemoryDataSource,
) : MemoryRepository {
    override suspend fun getMemory(memoryId: Long): ResponseResult<Memory> {
        return when (val responseResult = memoryDataSource.getMemory(memoryId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
        }
    }

    override suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse> {
        return when (val responseResult = memoryDataSource.createMemory(newMemory)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit> {
        return when (val responseResult = memoryDataSource.updateMemory(memoryId, newMemory)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit> {
        return when (val responseResult = memoryDataSource.deleteMemory(memoryId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    companion object {
        const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
    }
}
