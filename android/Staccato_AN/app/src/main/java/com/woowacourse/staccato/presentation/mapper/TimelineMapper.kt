package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

fun Timeline.toTimelineTravelUiModel(): List<TimelineTravelUiModel> {
    return memories.map { travel ->
        travel.toTimelineTravelUiModel()
    }
}

fun Memory.toTimelineTravelUiModel(): TimelineTravelUiModel {
    return TimelineTravelUiModel(
        travelId = travelId,
        travelTitle = travelTitle,
        travelThumbnailUrl = travelThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
    )
}
