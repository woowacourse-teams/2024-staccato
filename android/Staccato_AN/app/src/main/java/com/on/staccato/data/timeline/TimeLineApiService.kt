package com.on.staccato.data.timeline

import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.memory.MemoryApiService.Companion.CATEGORIES_PATH
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET(CATEGORIES_PATH)
    suspend fun getTimeline(
        @Query("year") year: Int? = null,
    ): Response<TimelineResponse>
}
