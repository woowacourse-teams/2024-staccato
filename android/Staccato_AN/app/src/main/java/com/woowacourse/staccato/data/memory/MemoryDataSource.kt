package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.domain.model.NewMemory
import okhttp3.MultipartBody

interface MemoryDataSource {
    suspend fun getMemory(memoryId: Long): ResponseResult<MemoryResponse>

    suspend fun createMemory(
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
