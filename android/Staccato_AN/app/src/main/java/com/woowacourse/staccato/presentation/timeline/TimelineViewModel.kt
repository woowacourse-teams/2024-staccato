package com.woowacourse.staccato.presentation.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.TimelineRepository
import com.woowacourse.staccato.presentation.common.Event
import com.woowacourse.staccato.presentation.mapper.toUiModel
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TimelineViewModel(private val repository: TimelineRepository) : ViewModel() {
    private val _travels = MutableLiveData<List<TimelineTravelUiModel>>()
    val travels: LiveData<List<TimelineTravelUiModel>>
        get() = _travels

    private val _errorState = MutableLiveData<Event<Boolean>>()
    val errorState: LiveData<Event<Boolean>>
        get() = _errorState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errorState.value = Event(true)
        Log.e("TimelineViewModel", "An error occurred: ${throwable.message}")
    }

    fun loadTimeline() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val timeline = repository.loadTravels()
            _travels.value = timeline.toUiModel()
        }
    }
}
