package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.model.NewTravel

interface TravelRepository {
    suspend fun getTravel(travelId: Long): ResponseResult<Travel>

    suspend fun createTravel(newTravel: NewTravel): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        newTravel: NewTravel,
    ): ResponseResult<String>
}
