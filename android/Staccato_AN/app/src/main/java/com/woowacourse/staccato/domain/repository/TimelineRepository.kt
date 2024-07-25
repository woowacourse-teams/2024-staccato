package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

interface TimelineRepository {
    suspend fun loadTravels(): List<TimelineTravelUiModel>

    fun loadTempTravels(): List<TimelineTravelUiModel>
}
