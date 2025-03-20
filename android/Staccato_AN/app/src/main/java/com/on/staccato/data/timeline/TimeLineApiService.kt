package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.timeline.TimelineResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET(CATEGORIES_PATH)
    suspend fun getTimeline(
        @Query(SORT) sort: String? = null,
        @Query(FILTERS) term: String? = null,
    ): ApiResult<TimelineResponse>

    companion object {
        const val CATEGORIES_PATH = "/categories"
        const val SORT = "sort"
        const val FILTERS = "filters"
    }
}
