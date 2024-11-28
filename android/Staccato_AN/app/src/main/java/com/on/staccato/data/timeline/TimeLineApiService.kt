package com.on.staccato.data.timeline

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.memory.MemoryApiService.Companion.MEMORIES_PATH
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET(MEMORIES_PATH)
    suspend fun getTimeline(
        @Query("year") year: Int? = null,
    ): ResponseResult<TimelineResponse>
}
