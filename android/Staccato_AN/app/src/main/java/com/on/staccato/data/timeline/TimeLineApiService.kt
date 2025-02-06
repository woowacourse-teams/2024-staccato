package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.category.CategoryApiService.Companion.CATEGORIES_PATH
import com.on.staccato.data.dto.timeline.TimelineResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET(CATEGORIES_PATH)
    suspend fun getTimeline(
        @Query("year") year: Int? = null,
    ): ApiResult<TimelineResponse>
}
