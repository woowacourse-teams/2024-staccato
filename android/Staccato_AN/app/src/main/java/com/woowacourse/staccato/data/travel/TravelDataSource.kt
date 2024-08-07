package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.domain.model.NewTravel

interface TravelDataSource {
    suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse>

    suspend fun createTravel(newTravel: NewTravel): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
    ): ResponseResult<String>

    suspend fun deleteTravel(travelId: Long): ResponseResult<Unit>
}
