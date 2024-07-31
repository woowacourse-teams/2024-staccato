package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationUiModel

interface TravelRepository {
    suspend fun getTravel(travelId: Long): ResponseResult<Travel>

    suspend fun createTravel(travelCreationUiModel: TravelCreationUiModel): ResponseResult<String>
}
