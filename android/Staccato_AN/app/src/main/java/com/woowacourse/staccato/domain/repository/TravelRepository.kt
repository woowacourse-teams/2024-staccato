package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.NewMemory
import com.woowacourse.staccato.domain.model.Memory
import okhttp3.MultipartBody

interface MemoryRepository {
    suspend fun getMemory(MemoryId: Long): ResponseResult<Memory>

    suspend fun createMemory(
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        MemoryId: Long,
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun deleteMemory(MemoryId: Long): ResponseResult<Unit>
}
