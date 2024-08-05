package com.woowacourse.staccato.data.dto.timeline

import com.woowacourse.staccato.data.dto.member.MembersDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineTravelDto(
    @SerialName("travelId") val travelId: Long,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("travelThumbnail") val travelThumbnail: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("mates") val mates: MembersDto,
)