package com.woowacourse.staccato.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.timeline.TimelineDefaultRepository

class TimelineViewModelFactory : ViewModelProvider.Factory {
    private val repository = TimelineDefaultRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(repository) as T
        } else {
            throw IllegalArgumentException("확인되지 않은 ViewModel 클래스입니다.")
        }
    }
}
