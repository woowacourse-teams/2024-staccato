package com.woowacourse.staccato.data.apiservice

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET("/travels")
    suspend fun requestTimeline(
        @Query("year") year: Int?,
    ): Response<TimelineResponse>
}
