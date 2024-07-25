package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

interface TimelineRepository {
    fun loadTravels(): List<TimelineTravelUiModel>
}
