package com.woowacourse.staccato.data.datasource

import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.apiservice.TimeLineApiService
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import org.json.JSONObject

class TimelineRemoteDataSource(
    private val service: TimeLineApiService = StaccatoClient.create(
        TimeLineApiService::class.java
    ),
) : TimelineDataSource {
    override suspend fun loadAll(): Result<TimelineResponse> {
        return loadTimeline()
    }

    override suspend fun loadByYear(year: Int): Result<TimelineResponse> {
        return loadTimeline(year)
    }

    private suspend fun loadTimeline(year: Int? = null): Result<TimelineResponse> {
        val response = service.requestTimeline(year)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            val errorBody = JSONObject(response.errorBody()?.string()!!)
            Result.failure(
                IllegalStateException(
                    "${errorBody.getString("status")} : ${errorBody.getString("messages")}"
                ),
            )
        }
    }
}
