package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.NewMemory
import com.woowacourse.staccato.domain.repository.MemoryRepository
import okhttp3.MultipartBody

class MemoryDefaultRepository(
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

    override suspend fun createMemory(
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse> {
        return when (val responseResult = memoryDataSource.createMemory(newMemory, thumbnailFile)) {
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
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String> {
        return when (
            val responseResult =
                memoryDataSource.updateMemory(memoryId, newMemory, thumbnailFile)
        ) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError ->
                ResponseResult.ServerError(
                    responseResult.status,
                    responseResult.message,
                )

            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun deleteMemory(MemoryId: Long): ResponseResult<Unit> {
        return when (val responseResult = memoryDataSource.deleteMemory(MemoryId)) {
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
