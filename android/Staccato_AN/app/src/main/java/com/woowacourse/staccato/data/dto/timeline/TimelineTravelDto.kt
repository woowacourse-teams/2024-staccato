package com.woowacourse.staccato.data.dto.timeline

import com.woowacourse.staccato.data.dto.member.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineMemoryDto(
    @SerialName("MemoryId") val MemoryId: Long,
    @SerialName("MemoryTitle") val MemoryTitle: String,
    @SerialName("MemoryThumbnailUrl") val MemoryThumbnailUrl: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("mates") val mates: List<MemberDto>,
)
