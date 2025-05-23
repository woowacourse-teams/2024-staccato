package com.on.staccato.data.timeline

import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeLineApiService {
    @GET(CATEGORIES_PATH_V2)
    suspend fun getTimeline(
        @Query(SORT) sort: String? = null,
        @Query(FILTERS) filter: String? = null,
    ): ApiResult<TimelineResponse>

    companion object {
        const val CATEGORIES_PATH_V2 = "/v2/categories"
        const val CATEGORIES_PATH = "/categories"
        const val SORT = "sort"
        const val FILTERS = "filters"
    }
}
