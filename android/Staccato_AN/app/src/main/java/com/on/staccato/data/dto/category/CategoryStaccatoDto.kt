package com.on.staccato.data.dto.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
<<<<<<<< HEAD:android/Staccato_AN/app/src/main/java/com/on/staccato/data/dto/category/CategoryStaccatoDto.kt
data class CategoryStaccatoDto(
    @SerialName("staccatoId") val staccatoId: Long,
========
data class MemoryStaccatoDto(
    @SerialName("momentId") val staccatoId: Long,
>>>>>>>> main-tmp:android/Staccato_AN/app/src/main/java/com/on/staccato/data/dto/category/MemoryStaccatoDto.kt
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("staccatoImageUrl") val staccatoImageUrl: String? = null,
    @SerialName("visitedAt") val visitedAt: String,
)
