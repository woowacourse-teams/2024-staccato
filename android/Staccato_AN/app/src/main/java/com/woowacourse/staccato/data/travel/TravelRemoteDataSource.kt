package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.APiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelResponse

class TravelRemoteDataSource(
    private val travelApiService: TravelApiService,
) : TravelDataSource {
    override suspend fun fetchTravel(travelId: Long): ResponseResult<TravelResponse> =
        handleApiResponse { travelApiService.requestTravel(travelId) }
}
