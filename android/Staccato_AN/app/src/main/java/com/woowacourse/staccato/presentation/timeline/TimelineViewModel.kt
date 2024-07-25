package com.woowacourse.staccato.presentation.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.domain.repository.TimelineRepository
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

class TimelineViewModel(private val repository: TimelineRepository) : ViewModel() {
    private val _travels = MutableLiveData<List<TimelineTravelUiModel>>()
    val travels: LiveData<List<TimelineTravelUiModel>>
        get() = _travels

    fun loadTimeline() {
        _travels.value = repository.loadTravels()
    }
}
