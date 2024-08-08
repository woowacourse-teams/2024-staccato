package com.woowacourse.staccato.presentation.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.repository.TimelineRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toTimelineTravelUiModel
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TimelineViewModel(private val timelineRepository: TimelineRepository) : ViewModel() {
    private val _travels = MutableLiveData<List<TimelineTravelUiModel>>()
    val travels: LiveData<List<TimelineTravelUiModel>>
        get() = _travels

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String>
        get() = _errorMessage

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("TimelineViewModel", "An error occurred: ${throwable.message}")
        }

    init {
        loadTimeline()
    }

    fun loadTimeline() {
        viewModelScope.launch(coroutineExceptionHandler) {
            timelineRepository.getTimeline()
                .onSuccess(::setTimelineTravelUiModels)
                .onServerError(::handleServerError)
                .onException(::handleException)
        }
    }

    private fun setTimelineTravelUiModels(timeline: Timeline) {
        _travels.value = timeline.toTimelineTravelUiModel()
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
