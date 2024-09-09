package com.on.staccato.presentation.visitupdate.model

import java.time.LocalDateTime

data class VisitUpdateDefaultUiModel(
    val id: Long,
    val address: String,
    val visitedAt: LocalDateTime,
)
