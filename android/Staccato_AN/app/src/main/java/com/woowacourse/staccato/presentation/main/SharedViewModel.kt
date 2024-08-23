package com.woowacourse.staccato.presentation.main

import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData

class SharedViewModel : ViewModel() {
    private val _isTimelineUpdated = MutableSingleLiveData(false)
    val isTimelineUpdated: SingleLiveData<Boolean>
        get() = _isTimelineUpdated

    private val _isStaccatosUpdated = MutableSingleLiveData(false)
    val isStaccatosUpdated: SingleLiveData<Boolean> get() = _isStaccatosUpdated

    fun setTimelineHasUpdated() {
        _isTimelineUpdated.setValue(true)
    }

    fun setStaccatosHasUpdated() {
        _isStaccatosUpdated.setValue(true)
    }
}
