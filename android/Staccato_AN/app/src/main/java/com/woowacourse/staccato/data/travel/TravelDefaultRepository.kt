package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.mapper.toDomain
import com.woowacourse.staccato.domain.mapper.toDomain
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationUiModel

class TravelDefaultRepository(
    private val travelDataSource: TravelDataSource,
) : TravelRepository {
    override suspend fun loadTravel(travelId: Long): ResponseResult<Travel> {
        return when (val responseResult = travelDataSource.fetchTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
        }
    }

    override suspend fun createTravel(travelCreationUiModel: TravelCreationUiModel): ResponseResult<String> {
        return when (val responseResult = travelDataSource.saveTravel(travelCreationUiModel.toDomain())) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
        }
    }

    companion object {
        const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
    }
}
