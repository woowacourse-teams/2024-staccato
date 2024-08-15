package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.data.dto.mapper.toMemoryUpdateRequest
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.domain.model.NewMemory
import okhttp3.MultipartBody

class MemoryRemoteDataSource(
    private val memoryApiService: MemoryApiService,
) : MemoryDataSource {
    override suspend fun getMemory(memoryId: Long): ResponseResult<MemoryResponse> =
        handleApiResponse { memoryApiService.getMemory(memoryId) }

    override suspend fun createMemory(
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse> =
        handleApiResponse {
            memoryApiService.postMemory(newMemory.toDto(), thumbnailFile)
        }

    override suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String> =
        handleApiResponse {
            memoryApiService.putMemory(memoryId, newMemory.toMemoryUpdateRequest(), thumbnailFile)
        }

    override suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit> = handleApiResponse { memoryApiService.deleteMemory(memoryId) }
}
