package com.on.staccato.data.timeline

import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mapper.toMemoryCandidates
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.TimelineRepository
import javax.inject.Inject

class TimelineDefaultRepository
    @Inject
    constructor(
        private val timelineDataSource: TimelineDataSource,
    ) : TimelineRepository {
        override suspend fun getTimeline(): ResponseResult<Timeline> {
            return when (val responseResult = timelineDataSource.getAllTimeline()) {
                is Success -> Success(responseResult.data.toDomain())
                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Exception ->
                    Exception(
                        responseResult.e,
                    )
            }
        }

        override suspend fun getMemoryCandidates(): ResponseResult<MemoryCandidates> {
            return when (val responseResult = timelineDataSource.getAllTimeline()) {
                is Success -> Success(responseResult.data.toMemoryCandidates())
                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Exception ->
                    Exception(
                        responseResult.e,
                    )
            }
        }
    }
