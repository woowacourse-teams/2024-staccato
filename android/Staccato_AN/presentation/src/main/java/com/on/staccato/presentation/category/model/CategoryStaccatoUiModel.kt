package com.on.staccato.presentation.category.model

import java.time.LocalDateTime

data class CategoryStaccatoUiModel(
    val id: Long,
    val staccatoTitle: String,
    val staccatoImageUrl: String? = null,
    val visitedAt: LocalDateTime,
)
