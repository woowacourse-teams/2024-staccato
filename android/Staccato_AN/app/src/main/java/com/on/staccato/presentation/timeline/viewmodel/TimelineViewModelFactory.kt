package com.on.staccato.presentation.timeline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.timeline.TimelineDefaultRepository
import com.on.staccato.domain.repository.TimelineRepository

class TimelineViewModelFactory(
    private val timelineRepository: TimelineRepository = TimelineDefaultRepository(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(timelineRepository) as T
        } else {
            throw IllegalArgumentException("확인되지 않은 ViewModel 클래스입니다.")
        }
    }
}
