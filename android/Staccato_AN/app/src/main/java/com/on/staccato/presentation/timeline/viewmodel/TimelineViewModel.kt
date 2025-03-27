package com.on.staccato.presentation.timeline.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toTimelineUiModel
import com.on.staccato.presentation.timeline.model.SortType
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel
    @Inject
    constructor(
        private val timelineRepository: TimelineRepository,
    ) : ViewModel() {
        private val _timeline = MutableLiveData<List<TimelineUiModel>>(emptyList())
        val timeline: LiveData<List<TimelineUiModel>>
            get() = _timeline

        private val _isTimelineLoading = MutableLiveData(false)
        val isTimelineLoading: LiveData<Boolean>
            get() = _isTimelineLoading

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _exceptionMessage = MutableSingleLiveData<String>()
        val exceptionMessage: SingleLiveData<String>
            get() = _exceptionMessage

        private var originalTimeline = listOf<TimelineUiModel>()

        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { context, throwable ->
                Log.e(
                    "TimelineViewModel",
                    "In coroutine context: $context\nAn error occurred: ${throwable.message}",
                )
            }

        init {
            loadTimeline()
        }

        fun loadTimeline() {
            _isTimelineLoading.value = true
            viewModelScope.launch(coroutineExceptionHandler) {
                timelineRepository.getTimeline()
                    .onSuccess(::setTimelineUiModels)
                    .onServerError(::handleServerError)
                    .onException(::handleException)
            }
        }

        fun sortTimeline(type: SortType) {
            when (type) {
                SortType.CREATION -> sortByCreation()
                SortType.LATEST -> sortByLatest()
                SortType.OLDEST -> sortByOldest()
                SortType.WITH_PERIOD -> filterWithPeriod()
                SortType.WITHOUT_PERIOD -> filterWithoutPeriod()
            }
        }

        private fun setTimelineUiModels(timeline: Timeline) {
            _timeline.value = timeline.toTimelineUiModel()
            originalTimeline = _timeline.value ?: emptyList()
            _isTimelineLoading.value = false
        }

        private fun sortByCreation() {
            _timeline.value = originalTimeline
        }

        private fun sortByLatest() {
            _timeline.value = originalTimeline.sortedByDescending { it.startAt }
        }

        private fun sortByOldest() {
            val categoriesSortedByOldest = originalTimeline.sortedWith(compareBy(nullsLast()) { it.startAt })
            _timeline.value = categoriesSortedByOldest
        }

        private fun filterWithPeriod() {
            _timeline.value = originalTimeline.filter { it.startAt != null }.sortedByDescending { it.startAt }
        }

        private fun filterWithoutPeriod() {
            _timeline.value = originalTimeline.filter { it.startAt == null }
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState) {
            _exceptionMessage.postValue(state.message)
        }
    }
