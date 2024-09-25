package com.on.staccato.domain.model

import java.time.LocalDateTime

data class MemoryMoment(
    val momentId: Long,
    val momentTitle: String,
    val momentImageUrl: String? = null,
    val visitedAt: LocalDateTime,
)
