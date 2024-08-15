package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.TravelCreationResponse
import com.woowacourse.staccato.data.dto.memory.TravelResponse
import com.woowacourse.staccato.domain.model.NewTravel
import okhttp3.MultipartBody

interface TravelDataSource {
    suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse>

    suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<TravelCreationResponse>

    suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun deleteTravel(travelId: Long): ResponseResult<Unit>
}
