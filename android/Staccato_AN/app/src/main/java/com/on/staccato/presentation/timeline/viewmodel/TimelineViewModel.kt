package com.on.staccato.presentation.timeline.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toTimelineUiModel
import com.on.staccato.presentation.timeline.model.FilterType
import com.on.staccato.presentation.timeline.model.SortType
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) : ViewModel() {
        private val _sortType = MutableLiveData(SortType.UPDATED)
        val sortType: LiveData<SortType>
            get() = _sortType

        private val _filterType = MutableLiveData<FilterType?>(null)
        val filterType: LiveData<FilterType?>
            get() = _filterType

        private val _timeline = MutableStateFlow<List<TimelineUiModel>>(emptyList())
        val timeline: StateFlow<List<TimelineUiModel>> = _timeline.asStateFlow()

        private val _isTimelineLoading = MutableLiveData(false)
        val isTimelineLoading: LiveData<Boolean>
            get() = _isTimelineLoading

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _exception = MutableSingleLiveData<ExceptionState2>()
        val exception: SingleLiveData<ExceptionState2>
            get() = _exception

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
                categoryRepository.getCategories(
                    sort = sortType.value?.name,
                    filter = filterType.value?.name,
                ).onSuccess(::setTimelineUiModels)
                    .onServerError(::handleServerError)
                    .onException2(::handleException)
            }
        }

        fun sortTimeline(type: SortType) {
            _sortType.value = type
            loadTimeline()
        }

        fun changeFilterState() {
            _filterType.value = FilterType.next(filterType.value)
            loadTimeline()
        }

        private fun setTimelineUiModels(timeline: Timeline) {
            _timeline.value = timeline.toTimelineUiModel()
            _isTimelineLoading.value = false
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.postValue(state)
        }
    }
