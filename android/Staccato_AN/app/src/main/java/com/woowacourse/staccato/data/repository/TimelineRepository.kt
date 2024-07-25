package com.woowacourse.staccato.data.repository

import com.woowacourse.staccato.data.datasource.TimelineDataSource
import com.woowacourse.staccato.data.datasource.TimelineRemoteDataSource
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.repository.TimelineRepository

class TimelineRepository(private val dataSource: TimelineDataSource = TimelineRemoteDataSource()) :
    TimelineRepository {
    override suspend fun loadTravels(): Timeline {
        var timeline: Timeline = Timeline(emptyList())
        val result = dataSource.fetchAll()
        result.onSuccess { timelineResponse ->
            timeline = timelineResponse.toDomain()
        }.onFailure {
            throw it
        }
        return timeline
    }

    override fun loadTempTravels(): Timeline {
        return Timeline(emptyList())
    }
}
