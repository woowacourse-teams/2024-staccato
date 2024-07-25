package com.woowacourse.staccato.data.dto.timeline

import com.woowacourse.staccato.data.dto.MemberDto
import com.woowacourse.staccato.data.dto.MembersDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineTravelDto(
    @SerialName("travelId") val travelId: Long,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("travelThumbnail") val travelThumbnail: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String,
    @SerialName("mates") val mates: MembersDto,
)
