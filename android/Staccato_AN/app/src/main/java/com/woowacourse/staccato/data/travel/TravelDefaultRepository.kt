package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.domain.model.NewTravel
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelDefaultRepository(
    private val travelDataSource: TravelDataSource,
) : TravelRepository {
    override suspend fun getTravel(travelId: Long): ResponseResult<Travel> {
        return when (val responseResult = travelDataSource.getTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
        }
    }

    override suspend fun createTravel(newTravel: NewTravel): ResponseResult<String> {
        return when (val responseResult = travelDataSource.createTravel(newTravel)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
    ): ResponseResult<String> {
        return when (val responseResult = travelDataSource.updateTravel(travelId, newTravel)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    override suspend fun deleteTravel(travelId: Long): ResponseResult<Unit> {
        return when (val responseResult = travelDataSource.deleteTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(Unit)
        }
    }

    companion object {
        const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
    }
}
