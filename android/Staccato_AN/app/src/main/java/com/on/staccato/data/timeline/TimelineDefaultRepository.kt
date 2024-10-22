package com.on.staccato.data.timeline

import com.on.staccato.data.ResponseResult
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
                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toDomain())
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Exception ->
                    ResponseResult.Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )
            }
        }

        override suspend fun getMemoryCandidates(): ResponseResult<MemoryCandidates> {
            return when (val responseResult = timelineDataSource.getAllTimeline()) {
                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.toMemoryCandidates())
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Exception ->
                    ResponseResult.Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 상태가 불안정 합니다."
        }
    }
