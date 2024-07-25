package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

interface TimelineRepository {
    fun loadTravels(): List<TimelineTravelUiModel>
}
