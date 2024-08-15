package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.domain.model.NewTravel
import okhttp3.MultipartBody

interface MemoryDataSource {
    suspend fun getTravel(travelId: Long): ResponseResult<MemoryResponse>

    suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse>

    suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun deleteTravel(travelId: Long): ResponseResult<Unit>
}
