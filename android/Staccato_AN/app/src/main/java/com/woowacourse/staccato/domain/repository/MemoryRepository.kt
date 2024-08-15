package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.NewMemory
import okhttp3.MultipartBody

interface MemoryRepository {
    suspend fun getTravel(travelId: Long): ResponseResult<Memory>

    suspend fun createTravel(
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse>

    suspend fun updateTravel(
        travelId: Long,
        newMemory: NewMemory,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun deleteTravel(travelId: Long): ResponseResult<Unit>
}
