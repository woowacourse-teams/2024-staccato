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

    private val isStaccatosUpdated = MutableSingleLiveData(false)

    private val _isPermissionCancelClicked = MutableLiveData(false)
    val isPermissionCancelClicked: LiveData<Boolean> get() = _isPermissionCancelClicked

    private val _isSettingClicked = MutableLiveData(false)
    val isSettingClicked: LiveData<Boolean> get() = _isSettingClicked

    fun setTimelineHasUpdated() {
        _isTimelineUpdated.setValue(true)
    }

    fun setStaccatosHasUpdated() {
        isStaccatosUpdated.setValue(true)
    }

    fun updateIsPermissionCancelClicked() {
        _isPermissionCancelClicked.value = true
    }

    fun updateIsSettingClicked(isSettingClicked: Boolean) {
        _isSettingClicked.value = isSettingClicked
    }
}
