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
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
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
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
