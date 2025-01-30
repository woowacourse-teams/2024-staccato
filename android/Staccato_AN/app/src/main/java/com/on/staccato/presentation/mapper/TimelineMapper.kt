package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineUiModel(): List<TimelineUiModel> {
    return categories.map { memory ->
        memory.toTimelineUiModel()
    }
}

fun Category.toTimelineUiModel(): TimelineUiModel {
    return TimelineUiModel(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        categoryThumbnailUrl = categoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
