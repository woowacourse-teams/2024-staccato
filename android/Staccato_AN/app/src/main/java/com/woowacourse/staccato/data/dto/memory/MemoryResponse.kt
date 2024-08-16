package com.woowacourse.staccato.data.dto.memory

import com.woowacourse.staccato.data.dto.member.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryResponse(
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("memoryThumbnailUrl") val memoryThumbnailUrl: String? = null,
    @SerialName("memoryTitle") val memoryTitle: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("mates") val mates: List<MemberDto>,
    @SerialName("moments") val moments: List<MemoryMomentDto>,
)
