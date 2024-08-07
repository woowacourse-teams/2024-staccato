package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.domain.model.NewTravel

class TravelRemoteDataSource(
    private val travelApiService: TravelApiService,
) : TravelDataSource {
    override suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse> =
        handleApiResponse { travelApiService.getTravel(travelId) }

    override suspend fun createTravel(newTravel: NewTravel): ResponseResult<String> =
        handleApiResponse {
            travelApiService.postTravel(newTravel.toDto())
        }

    override suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
    ): ResponseResult<String> =
        handleApiResponse {
            travelApiService.putTravel(travelId, newTravel.toDto())
        }

    override suspend fun deleteTravel(travelId: Long): ResponseResult<Unit> = handleApiResponse { travelApiService.deleteTravel(travelId) }
}
