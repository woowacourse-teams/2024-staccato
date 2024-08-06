package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.model.TravelCreation

interface TravelRepository {
    suspend fun getTravel(travelId: Long): ResponseResult<Travel>

    suspend fun createTravel(travelCreation: TravelCreation): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        travelCreation: TravelCreation,
    ): ResponseResult<String>
}
