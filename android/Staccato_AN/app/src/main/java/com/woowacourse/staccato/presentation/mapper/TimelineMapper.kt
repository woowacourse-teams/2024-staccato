package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.presentation.timeline.model.TimelineMemoryUiModel

fun Timeline.toTimelineMemoryUiModel(): List<TimelineMemoryUiModel> {
    return memories.map { Memory ->
        Memory.toTimelineMemoryUiModel()
    }
}

fun Memory.toTimelineMemoryUiModel(): TimelineMemoryUiModel {
    return TimelineMemoryUiModel(
        MemoryId = memoryId,
        MemoryTitle = memoryTitle,
        MemoryThumbnailUrl = memoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
