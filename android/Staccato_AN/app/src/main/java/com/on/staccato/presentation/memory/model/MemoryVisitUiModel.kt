package com.on.staccato.presentation.memory.model

import java.time.LocalDateTime

data class MemoryVisitUiModel(
    val id: Long,
    val staccatoTitle: String,
    val visitImageUrl: String? = null,
    val visitedAt: LocalDateTime,
)
