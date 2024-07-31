package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import org.json.JSONObject

class TimelineRemoteDataSource(
    private val service: TimeLineApiService = StaccatoClient.timelineService,
) : TimelineDataSource {
    override suspend fun getAllTimeline(): Result<TimelineResponse> {
        return fetchTimeline()
    }

    override suspend fun getTimeline(year: Int): Result<TimelineResponse> {
        return fetchTimeline(year)
    }

    private suspend fun fetchTimeline(year: Int? = null): Result<TimelineResponse> {
        val response = service.getTimeline(year)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            val errorBody = JSONObject(response.errorBody()?.string()!!)
            Result.failure(
                IllegalStateException(
                    "${errorBody.getString("status")} : ${errorBody.getString("message")}",
                ),
            )
        }
    }
}
