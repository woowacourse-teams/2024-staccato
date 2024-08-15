package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.NewTravel
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository
import okhttp3.MultipartBody

class TravelDefaultRepository(
    private val memoryDataSource: MemoryDataSource,
) : TravelRepository {
    override suspend fun getTravel(travelId: Long): ResponseResult<Travel> {
        return when (val responseResult = memoryDataSource.getTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
        }
    }

    override suspend fun createTravel(
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<MemoryCreationResponse> {
        return when (val responseResult = memoryDataSource.createTravel(newTravel, thumbnailFile)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
        thumbnailFile: MultipartBody.Part?,
    ): ResponseResult<String> {
        return when (val responseResult = memoryDataSource.updateTravel(travelId, newTravel, thumbnailFile)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun deleteTravel(travelId: Long): ResponseResult<Unit> {
        return when (val responseResult = memoryDataSource.deleteTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    companion object {
        const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
    }
}
