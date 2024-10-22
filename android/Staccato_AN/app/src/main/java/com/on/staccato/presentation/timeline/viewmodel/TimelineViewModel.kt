package com.on.staccato.presentation.timeline.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toTimelineUiModel
import com.on.staccato.presentation.timeline.model.TimelineUiModel
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

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _isTimelineLoading = MutableLiveData(false)
        val isTimelineLoading: LiveData<Boolean>
            get() = _isTimelineLoading

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

        fun sortByLatest() {
            _timeline.value = _timeline.value?.sortedByDescending { it.startAt }
        }

        fun sortByOldest() {
            val memoriesSortedByOldest = _timeline.value?.sortedWith(compareBy(nullsLast()) { it.startAt }) ?: emptyList()
            _timeline.value = memoriesSortedByOldest
        }

        private fun setTimelineUiModels(timeline: Timeline) {
            _timeline.value = timeline.toTimelineUiModel()
            _isTimelineLoading.value = false
        }

        private fun handleServerError(
            status: Status,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
            when (status) {
                is Status.Code ->
                    Log.e(
                        "TimelineViewModel",
                        "An error occurred: ${status.code}, $errorMessage",
                    )

                is Status.Message ->
                    Log.e(
                        "TimelineViewModel",
                        "An error occurred: ${status.message}, $errorMessage",
                    )
            }
        }

        private fun handleException(
            e: Throwable,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
            Log.e("TimelineViewModel", "An exception occurred: $e, $errorMessage")
        }
    }
