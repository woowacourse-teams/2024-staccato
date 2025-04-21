package com.on.staccato.presentation.timeline.model

import androidx.annotation.ColorRes
import java.time.LocalDate

data class TimelineUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val categoryThumbnailUrl: String? = null,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    @ColorRes val color: Int,
)
