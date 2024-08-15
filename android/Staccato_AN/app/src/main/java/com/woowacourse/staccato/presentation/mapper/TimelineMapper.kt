package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineTravelUiModel(): List<TimelineUiModel> {
    return memories.map { travel ->
        travel.toTimelineTravelUiModel()
    }
}

fun Memory.toTimelineTravelUiModel(): TimelineUiModel {
    return TimelineUiModel(
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        memoryThumbnailUrl = memoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
