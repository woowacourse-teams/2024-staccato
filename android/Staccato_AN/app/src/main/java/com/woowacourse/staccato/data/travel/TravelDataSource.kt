package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelRequest
import com.woowacourse.staccato.data.dto.travel.TravelResponse

interface TravelDataSource {
    suspend fun fetchTravel(travelId: Long): ResponseResult<TravelResponse>

    suspend fun saveTravel(travelRequest: TravelRequest): ResponseResult<Unit>
}
