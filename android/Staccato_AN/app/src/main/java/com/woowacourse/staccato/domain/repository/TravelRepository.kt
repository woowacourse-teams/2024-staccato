package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.NewTravel
import com.woowacourse.staccato.domain.model.Travel
import okhttp3.MultipartBody

interface TravelRepository {
    suspend fun getTravel(travelId: Long): ResponseResult<Travel>

    suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
    ): ResponseResult<String>

    suspend fun deleteTravel(travelId: Long): ResponseResult<Unit>
}
