package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationUiModel

interface TravelRepository {
    suspend fun loadTravel(travelId: Long): ResponseResult<Travel>

    suspend fun createTravel(travelCreationUiModel: TravelCreationUiModel): ResponseResult<String>

    suspend fun updateTravel(
        travelId: Long,
        travelCreationUiModel: TravelCreationUiModel,
    ): ResponseResult<String>
}
