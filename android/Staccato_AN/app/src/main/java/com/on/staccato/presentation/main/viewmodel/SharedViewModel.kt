package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.domain.repository.MyPageRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
    @Inject
    constructor(private val myPageRepository: MyPageRepository) : ViewModel() {
        private val _memberProfile = MutableLiveData<MemberProfile>()
        val memberProfile: LiveData<MemberProfile> get() = _memberProfile

        private val _isTimelineUpdated = MutableSingleLiveData(false)
        val isTimelineUpdated: SingleLiveData<Boolean>
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

        private val _isHalf = MutableLiveData(true)
        val isHalf: LiveData<Boolean> get() = _isHalf

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _staccatoLocation = MutableLiveData<StaccatoLocation>()
        val staccatoLocation: LiveData<StaccatoLocation> get() = _staccatoLocation

        fun fetchMemberProfile() {
            viewModelScope.launch {
                val result = myPageRepository.getMemberProfile()
                result.onException(::handleException)
                    .onServerError(::handleServerError)
                    .onSuccess(::setMemberProfile)
            }
        }

        fun setTimelineHasUpdated() {
            _isTimelineUpdated.setValue(true)
        }

        fun setStaccatosHasUpdated() {
            _isStaccatosUpdated.setValue(true)
        }

        fun updateIsPermissionCanceled() {
            _isPermissionCanceled.value = true
        }

        fun updateIsSettingClicked(isSettingClicked: Boolean) {
            _isSettingClicked.value = isSettingClicked
        }

        fun setIsHalf(isHalf: Boolean) {
            _isHalf.value = isHalf
        }

        fun updateStaccatoId(id: Long) {
            _staccatoId.value = id
        }

        fun updateStaccatoLocation(
            staccatoId: Long,
            latitude: Double,
            longitude: Double,
        ) {
            _staccatoLocation.value = StaccatoLocation(staccatoId, latitude, longitude)
        }

        private fun setMemberProfile(memberProfile: MemberProfile) {
            _memberProfile.value = memberProfile
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(exceptionState: ExceptionState) {
            _errorMessage.postValue(exceptionState.message)
        }
    }
