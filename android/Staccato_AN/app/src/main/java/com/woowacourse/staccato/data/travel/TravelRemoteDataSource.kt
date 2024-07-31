package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.domain.model.TravelCreation

class TravelRemoteDataSource(
    private val travelApiService: TravelApiService,
) : TravelDataSource {
    override suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse> =
        handleApiResponse { travelApiService.getTravel(travelId) }

    override suspend fun createTravel(travelCreation: TravelCreation): ResponseResult<String> =
        handleApiResponse {
            travelApiService.postTravel(travelCreation.toDto())
        }
}
