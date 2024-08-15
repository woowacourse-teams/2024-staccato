package com.woowacourse.staccato.data.dto.memory

import com.woowacourse.staccato.data.dto.member.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryResponse(
    @SerialName("travelId") val travelId: Long,
    @SerialName("travelThumbnailUrl") val travelThumbnailUrl: String? = null,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("mates") val mates: List<MemberDto>,
    @SerialName("visits") val visits: List<TravelVisitDto>,
)
