package com.on.staccato.data.dto.category

import com.on.staccato.data.dto.member.MemberDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryThumbnailUrl") val categoryThumbnailUrl: String? = null,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("startAt") val startAt: String? = null,
    @SerialName("endAt") val endAt: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("categoryColor") val color: String,
    @SerialName("mates") val mates: List<MemberDto>,
    @SerialName("staccatos") val staccatos: List<CategoryStaccatoDto>,
)
