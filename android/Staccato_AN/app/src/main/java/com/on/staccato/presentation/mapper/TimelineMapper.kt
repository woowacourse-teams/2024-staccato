package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineUiModel(): List<TimelineUiModel> {
    return memories.map { memory ->
        memory.toTimelineUiModel()
    }
}

fun Memory.toTimelineUiModel(): TimelineUiModel {
    return TimelineUiModel(
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        memoryThumbnailUrl = memoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
