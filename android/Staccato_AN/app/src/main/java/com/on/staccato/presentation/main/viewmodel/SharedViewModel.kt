package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData

class SharedViewModel : ViewModel() {
    private val _isTimelineUpdated = MutableSingleLiveData(false)
    val isTimelineUpdated: SingleLiveData<Boolean>
        get() = _isTimelineUpdated

    private val _isStaccatosUpdated = MutableSingleLiveData(false)
    val isStaccatosUpdated: SingleLiveData<Boolean> get() = _isStaccatosUpdated

    private val _isPermissionCancelClicked = MutableLiveData(false)
    val isPermissionCancelClicked: LiveData<Boolean> get() = _isPermissionCancelClicked

    fun setTimelineHasUpdated() {
        _isTimelineUpdated.setValue(true)
    }

    fun setStaccatosHasUpdated() {
        _isStaccatosUpdated.setValue(true)
    }

    fun updateIsPermissionCancelClicked() {
        _isPermissionCancelClicked.value = true
    }
}
