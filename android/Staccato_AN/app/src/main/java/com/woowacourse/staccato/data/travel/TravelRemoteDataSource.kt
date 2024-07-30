package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.data.mapper.toDto
import com.woowacourse.staccato.domain.model.TravelCreation

class TravelRemoteDataSource(
    private val travelApiService: TravelApiService,
) : TravelDataSource {
    override suspend fun fetchTravel(travelId: Long): ResponseResult<TravelResponse> =
        handleApiResponse { travelApiService.requestTravel(travelId) }

    override suspend fun saveTravel(travelCreation: TravelCreation): ResponseResult<String> =
        handleApiResponse {
            travelApiService.addTravel(travelCreation.toDto())
        }

    override suspend fun updateTravel(
        travelId: Long,
        travelCreation: TravelCreation,
    ): ResponseResult<String> =
        handleApiResponse {
            travelApiService.updateTravel(travelId, travelCreation.toDto())
        }
}
