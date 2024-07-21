package com.woowacourse.staccato.data.dto.travel

import com.woowacourse.staccato.data.dto.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelDto(
    @SerialName("travel_id") val travelId: Long,
    @SerialName("travel_thumbnail") val travelThumbnail: String,
    @SerialName("travel_title") val travelTitle: String,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("description") val description: String,
    @SerialName("mates") val mates: List<MemberDto>,
    @SerialName("visits") val visits: List<TravelVisitDto>,
)
