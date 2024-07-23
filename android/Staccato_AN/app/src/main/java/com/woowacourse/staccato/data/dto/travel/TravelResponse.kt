package com.woowacourse.staccato.data.dto.travel

import com.woowacourse.staccato.data.dto.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelResponse(
    @SerialName("travelId") val travelId: Long,
    @SerialName("travelThumbnail") val travelThumbnail: String,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String,
    @SerialName("mates") val mates: List<MemberDto>,
    @SerialName("visits") val visits: List<TravelVisitDto>,
)
