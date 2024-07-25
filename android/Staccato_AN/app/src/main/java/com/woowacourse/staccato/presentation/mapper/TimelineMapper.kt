package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.presentation.converter.convertLocalDateToDatePeriodString
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

fun Timeline.toUiModel(): List<TimelineTravelUiModel> {
    return travels.map { travel ->
        travel.toUiModel()
    }
}

fun Travel.toUiModel(): TimelineTravelUiModel {
    return TimelineTravelUiModel(
        travelId = travelId,
        travelThumbnail = travelThumbnail,
        travelPeriod = convertLocalDateToDatePeriodString(startAt, endAt),
        travelTitle = travelTitle,
    )
}
