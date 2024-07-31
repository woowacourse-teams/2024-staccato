package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET("/travels")
    suspend fun getTimeline(
        @Query("year") year: Int? = null,
    ): Response<TimelineResponse>
}
