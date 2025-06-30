package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.TimeLineCategory
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.timeline.model.ParticipantsUiModel
import com.on.staccato.presentation.timeline.model.TimelineUiModel

fun Timeline.toTimelineUiModel(): List<TimelineUiModel> {
    return timelineCategories.map {
        it.toTimelineUiModel()
    }
}

fun TimeLineCategory.toTimelineUiModel(): TimelineUiModel {
    return TimelineUiModel(
        categoryId = categoryId,
        categoryThumbnailUrl = categoryThumbnailUrl,
        categoryTitle = categoryTitle,
        color = CategoryColor.getCategoryColorBy(color),
        isShared = isShared,
        startAt = startAt,
        endAt = endAt,
        participants = ParticipantsUiModel.from(members, totalMemberCount),
        staccatoCount = staccatoCount,
    )
}
