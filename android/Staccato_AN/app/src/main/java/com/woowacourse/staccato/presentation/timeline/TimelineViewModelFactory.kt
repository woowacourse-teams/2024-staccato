package com.woowacourse.staccato.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.timeline.TimelineRepository

class TimelineViewModelFactory : ViewModelProvider.Factory {
    private val repository = TimelineRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(repository) as T
        } else {
            throw IllegalArgumentException("확인되지 않은 ViewModel 클래스입니다.")
        }
    }
}
