package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.data.dto.mapper.toTravelUpdateRequest
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.domain.model.NewTravel
import okhttp3.MultipartBody

class MemoryRemoteDataSource(
    private val memoryApiService: MemoryApiService,
) : MemoryDataSource {
    override suspend fun getTravel(travelId: Long): ResponseResult<MemoryResponse> =
        handleApiResponse { memoryApiService.getTravel(travelId) }

    override suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse> =
        handleApiResponse {
            memoryApiService.postTravel(newTravel.toDto(), thumbnailFile)
        }

    override suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String> =
        handleApiResponse {
            memoryApiService.putTravel(travelId, newTravel.toTravelUpdateRequest(), thumbnailFile)
        }

    override suspend fun deleteTravel(travelId: Long): ResponseResult<Unit> = handleApiResponse { memoryApiService.deleteTravel(travelId) }
}
