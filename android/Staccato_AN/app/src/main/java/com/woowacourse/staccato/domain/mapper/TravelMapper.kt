package com.woowacourse.staccato.domain.mapper

import com.woowacourse.staccato.domain.model.TravelCreation
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationUiModel

fun TravelCreationUiModel.toDomain() =
    TravelCreation(
        travelThumbnail = travelThumbnail,
        travelTitle = travelTitle,
        startAt = startAt,
        endAt = endAt,
        description = description,
    )
