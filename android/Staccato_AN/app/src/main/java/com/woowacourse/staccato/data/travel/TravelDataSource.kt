package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.domain.model.TravelCreation

interface TravelDataSource {
    suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse>

    suspend fun createTravel(travelCreation: TravelCreation): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        travelCreation: TravelCreation,
    ): ResponseResult<String>
}
