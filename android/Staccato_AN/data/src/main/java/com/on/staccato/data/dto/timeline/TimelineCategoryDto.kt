package com.on.staccato.data.dto.timeline

import com.on.staccato.data.dto.member.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineCategoryDto(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryThumbnailUrl") val categoryThumbnailUrl: String? = null,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("categoryColor") val color: String,
    @SerialName("startAt") val startAt: String? = null,
    @SerialName("endAt") val endAt: String? = null,
    @SerialName("isShared") val isShared: Boolean,
    @SerialName("totalMemberCount") val totalMemberCount: Long,
    @SerialName("members") val members: List<MemberDto>,
    @SerialName("staccatoCount") val staccatoCount: Long,
)
