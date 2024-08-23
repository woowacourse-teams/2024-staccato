package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.moment.MomentLocationDto
import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.comment.CommentDto
import com.woowacourse.staccato.domain.model.Feeling
import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.MomentLocation
import com.woowacourse.staccato.domain.model.Comment
import java.time.LocalDateTime

fun MomentResponse.toDomain() =
    Moment(
        momentId = momentId,
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        placeName = placeName,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = LocalDateTime.parse(visitedAt),
        feeling = Feeling.fromValue(feeling),
        comments = visitLogs.map { it.toDomain() },
    )

fun MomentLocationDto.toDomain() =
    MomentLocation(
        momentId = momentId,
        latitude = latitude,
        longitude = longitude,
    )
