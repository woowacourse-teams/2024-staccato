package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Travel

interface TravelRepository {
    suspend fun loadTravel(): ResponseResult<Travel>
}
