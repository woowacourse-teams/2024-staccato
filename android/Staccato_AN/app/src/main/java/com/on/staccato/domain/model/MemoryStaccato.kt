package com.on.staccato.domain.model

import java.time.LocalDateTime

data class MemoryStaccato(
    val staccatoId: Long,
    val staccatoTitle: String,
    val staccatoImageUrl: String? = null,
    val visitedAt: LocalDateTime,
)
