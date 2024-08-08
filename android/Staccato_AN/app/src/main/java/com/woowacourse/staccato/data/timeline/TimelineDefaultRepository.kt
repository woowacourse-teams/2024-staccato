package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.repository.TimelineRepository

class TimelineDefaultRepository(
    private val timelineDataSource: TimelineDataSource = TimelineRemoteDataSource(),
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
                    EXCEPTION_ERROR_MESSAGE,
                )
        }
    }

    companion object {
        private const val EXCEPTION_ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다.\n잠시 후에 다시 시도해 주세요."
    }
}
