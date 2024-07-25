package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.travel.TravelResponse

interface TravelDataSource {
    suspend fun fetchTravel(): ResponseResult<TravelResponse>
}
