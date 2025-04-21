package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.TimeLineCategory
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineUiModel(): List<TimelineUiModel> {
    return timelineCategories.map {
        it.toTimelineUiModel()
    }
}

fun TimeLineCategory.toTimelineUiModel(): TimelineUiModel {
    return TimelineUiModel(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        categoryThumbnailUrl = categoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
        color = CategoryColor.getColorResBy(color),
    )
}
