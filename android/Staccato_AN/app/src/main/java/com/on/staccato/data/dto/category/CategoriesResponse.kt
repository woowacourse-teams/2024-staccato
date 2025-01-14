package com.on.staccato.data.dto.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponse(
    @SerialName("categories") val categories: List<CategoryCandidateResponse>,
)

@Serializable
data class CategoryCandidateResponse(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
)
