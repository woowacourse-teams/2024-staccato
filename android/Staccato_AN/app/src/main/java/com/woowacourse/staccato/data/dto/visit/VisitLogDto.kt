package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitLogDto(
    @SerialName("visit_log_id") val visitLogId: Long,
    @SerialName("member_id") val memberId: Long,
    @SerialName("nick_name") val nickName: String,
    @SerialName("member_image") val memberImage: String,
    @SerialName("content") val content: String,
)
