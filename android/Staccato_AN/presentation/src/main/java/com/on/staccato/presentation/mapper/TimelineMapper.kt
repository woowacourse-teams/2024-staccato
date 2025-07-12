package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.TimeLineCategory
import com.on.staccato.domain.model.Timeline
import com.on.staccato.presentation.color.CategoryColor
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.ParticipantsUiModel

fun Timeline.toTimelineUiModel(): List<CategoryUiModel> {
    return timelineCategories.map {
        it.toTimelineUiModel()
    }
}

fun TimeLineCategory.toTimelineUiModel(): CategoryUiModel {
    return CategoryUiModel(
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
