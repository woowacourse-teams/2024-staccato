package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.presentation.timeline.model.TimelineUiModel

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
