package com.woowacourse.staccato.data.dto.comment

import com.woowacourse.staccato.data.dto.moment.VisitLogDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsResponse(
    @SerialName("comments") val comments: List<VisitLogDto>
)
