package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException2
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
    @Inject
    constructor(private val myPageRepository: MyPageRepository) : ViewModel() {
        private val _memberProfile = MutableLiveData<MemberProfile>()
        val memberProfile: LiveData<MemberProfile> get() = _memberProfile

        private val _isTimelineUpdated = MutableLiveData(false)
        val isTimelineUpdated: LiveData<Boolean>
            get() = _isTimelineUpdated

        private val _isStaccatosUpdated = MutableSingleLiveData(false)
        val isStaccatosUpdated: SingleLiveData<Boolean>
            get() = _isStaccatosUpdated

        private val _isPermissionCanceled = MutableLiveData(false)
        val isPermissionCanceled: LiveData<Boolean> get() = _isPermissionCanceled

        private val _isSettingClicked = MutableLiveData(false)
        val isSettingClicked: LiveData<Boolean> get() = _isSettingClicked

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exception = MutableSingleLiveData<ExceptionState2>()
        val exception: SingleLiveData<ExceptionState2> get() = _exception

        private val _isBottomSheetHalf = MutableLiveData(true)
        val isBottomSheetHalf: LiveData<Boolean> get() = _isBottomSheetHalf

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _staccatoLocation = MutableLiveData<LocationUiModel>()
        val staccatoLocation: LiveData<LocationUiModel> get() = _staccatoLocation

        private val isDragging = MutableLiveData<Boolean>(false)

        fun fetchMemberProfile() {
            viewModelScope.launch {
                val result = myPageRepository.getMemberProfile()
                result.onException2(::handleException)
                    .onServerError(::handleServerError)
                    .onSuccess(::setMemberProfile)
            }
        }

        fun setTimelineHasUpdated() {
            _isTimelineUpdated.value = true
        }

        fun setStaccatosHasUpdated() {
            _isTimelineUpdated.setValue(true)
            _isStaccatosUpdated.setValue(true)
        }

        fun updateIsPermissionCanceled() {
            _isPermissionCanceled.value = true
        }

        fun updateIsSettingClicked(isSettingClicked: Boolean) {
            _isSettingClicked.value = isSettingClicked
        }

        fun setIsBottomSheetHalf(isBottomSheetHalf: Boolean) {
            val isDifferent = isBottomSheetHalf != _isBottomSheetHalf.value
            if (isDifferent && isDragging.value == false) _isBottomSheetHalf.value = isBottomSheetHalf
        }

        fun updateStaccatoId(id: Long) {
            _staccatoId.value = id
        }

        fun updateStaccatoLocation(
            latitude: Double,
            longitude: Double,
        ) {
            _staccatoLocation.value = LocationUiModel(latitude, longitude)
        }

        fun updateIsDragging(state: Boolean) {
            isDragging.value = state
        }

        private fun setMemberProfile(memberProfile: MemberProfile) {
            _memberProfile.value = memberProfile
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.setValue(state)
        }
    }
