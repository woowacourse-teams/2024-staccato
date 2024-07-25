package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.mapper.toDomain
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelDefaultRepository(
    private val travelDataSource: TravelDataSource,
) : TravelRepository {
    override suspend fun loadTravel(travelId: Long): ResponseResult<Travel> {
        return when (val responseResult = travelDataSource.fetchTravel(travelId)) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, "예기치 않은 오류가 발생했습니다")
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.code, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
        }
    }
}
