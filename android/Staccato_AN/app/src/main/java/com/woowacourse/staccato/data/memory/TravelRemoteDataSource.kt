package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.data.dto.mapper.toTravelUpdateRequest
import com.woowacourse.staccato.data.dto.memory.TravelCreationResponse
import com.woowacourse.staccato.data.dto.memory.TravelResponse
import com.woowacourse.staccato.domain.model.NewTravel
import okhttp3.MultipartBody

class TravelRemoteDataSource(
    private val travelApiService: TravelApiService,
) : TravelDataSource {
    override suspend fun getTravel(travelId: Long): ResponseResult<TravelResponse> =
        handleApiResponse { travelApiService.getTravel(travelId) }

    override suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<TravelCreationResponse> =
        handleApiResponse {
            travelApiService.postTravel(newTravel.toDto(), thumbnailFile)
        }

    override suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String> =
        handleApiResponse {
            travelApiService.putTravel(travelId, newTravel.toTravelUpdateRequest(), thumbnailFile)
        }

    override suspend fun deleteTravel(travelId: Long): ResponseResult<Unit> = handleApiResponse { travelApiService.deleteTravel(travelId) }
}
