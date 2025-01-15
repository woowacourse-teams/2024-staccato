package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineUiModel(): List<TimelineUiModel> {
    return memories.map { memory ->
        memory.toTimelineUiModel()
    }
}

fun Category.toTimelineUiModel(): TimelineUiModel {
    return TimelineUiModel(
        memoryId = categoryId,
        memoryTitle = categoryTitle,
        memoryThumbnailUrl = categoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
