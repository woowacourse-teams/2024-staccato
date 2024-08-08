package com.woowacourse.staccato.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData

class SharedViewModel : ViewModel() {
    private val _isTimelineUpdated = MutableSingleLiveData(false)
    val isTimelineUpdated: SingleLiveData<Boolean>
        get() = _isTimelineUpdated

    fun setTimelineHasUpdated() {
        _isTimelineUpdated.setValue(true)
        Log.d("hodu", "update state changed")
    }
}
