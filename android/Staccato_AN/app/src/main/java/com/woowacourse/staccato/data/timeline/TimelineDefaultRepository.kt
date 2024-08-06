package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.repository.TimelineRepository

class TimelineDefaultRepository(
    private val dataSource: TimelineDataSource = TimelineRemoteDataSource(),
) : TimelineRepository {
    override suspend fun getTimeline(): Timeline {
        var timeline: Timeline = Timeline(emptyList())
        val result = dataSource.getAllTimeline()
        result.onSuccess { timelineResponse ->
            timeline = timelineResponse.toDomain()
        }.onFailure {
            throw it
        }
        return timeline
    }
}
