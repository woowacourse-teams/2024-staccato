package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
    @Inject
    constructor(
        private val myPageRepository: MyPageRepository,
        private val notificationRepository: NotificationRepository,
    ) : ViewModel() {
        private val _halfModeEvent = MutableSharedFlow<Unit>()
        val halfModeEvent: SharedFlow<Unit> = _halfModeEvent.asSharedFlow()

        private val _isAtTop = MutableStateFlow<Boolean>(true)
        val isAtTop: StateFlow<Boolean> = _isAtTop.asStateFlow()

        private val _isDraggable = MutableLiveData<Boolean>(true)
        val isDraggable: LiveData<Boolean> get() = _isDraggable

        private val _latestIsDraggable = MutableLiveData<Boolean>(_isDraggable.value)
        val latestIsDraggable: LiveData<Boolean> get() = _latestIsDraggable

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

        private val _isBottomSheetExpanded = MutableLiveData<Boolean>(false)
        val isBottomSheetExpanded: LiveData<Boolean> get() = _isBottomSheetExpanded

        private val _isBottomSheetHalfExpanded = MutableLiveData(true)
        val isBottomSheetHalfExpanded: LiveData<Boolean> get() = _isBottomSheetHalfExpanded

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _staccatoLocation = MutableLiveData<LocationUiModel>()
        val staccatoLocation: LiveData<LocationUiModel> get() = _staccatoLocation

        private val _exception = MutableLiveData<ExceptionState2>()
        val exception: LiveData<ExceptionState2> get() = _exception

        private val _retryEvent = MutableSharedFlow<Unit>()
        val retryEvent: SharedFlow<Unit> = _retryEvent.asSharedFlow()

        private val isDragging = MutableLiveData<Boolean>(false)

        private val _hasNotification = MutableLiveData<Boolean>(false)
        val hasNotification: LiveData<Boolean> get() = _hasNotification

        fun fetchMemberProfile() {
            viewModelScope.launch {
                val result = myPageRepository.getMemberProfile()
                result.onException2(::handleException)
                    .onServerError(::handleServerError)
                    .onSuccess(::setMemberProfile)
            }
        }

        fun fetchNotificationExistence() {
            viewModelScope.launch {
                notificationRepository.getNotificationExistence()
                    .onSuccess { _hasNotification.value = it.isExist }
                    .onServerError(::handleServerError)
                    .onException2(::handleException)
            }
        }

        fun updateIsAtTop(value: Boolean) {
            _isAtTop.value = value
        }

        fun updateIsDraggable() {
            val isHalfExpanded = _isBottomSheetHalfExpanded.value == true
            val isExpandedAndTop = _isBottomSheetExpanded.value == true && _isAtTop.value
            val isCollapsed = _isBottomSheetExpanded.value == false && _isBottomSheetHalfExpanded.value == false

            _isDraggable.value = isHalfExpanded || isExpandedAndTop || isCollapsed
        }

        fun updateLatestIsDraggable() {
            _latestIsDraggable.value = _isDraggable.value
        }

        fun setTimelineHasUpdated() {
            _isTimelineUpdated.value = true
        }

        fun setStaccatosHasUpdated() {
            _isTimelineUpdated.value = true
            _isStaccatosUpdated.setValue(true)
        }

        fun updateIsPermissionCanceled() {
            _isPermissionCanceled.value = true
        }

        fun updateIsSettingClicked(isSettingClicked: Boolean) {
            _isSettingClicked.value = isSettingClicked
        }

        fun updateBottomSheetState(
            isExpanded: Boolean,
            isHalfExpanded: Boolean,
        ) {
            val isDifferent = isHalfExpanded != _isBottomSheetHalfExpanded.value
            if (isDifferent && isDragging.value == false) _isBottomSheetHalfExpanded.value = isHalfExpanded
            _isBottomSheetExpanded.value = isExpanded
        }

        fun updateHalfModeEvent() {
            viewModelScope.launch {
                _halfModeEvent.emit(Unit)
            }
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

        fun updateException(state: ExceptionState2) {
            _exception.value = state
        }

        fun updateIsRetry() {
            viewModelScope.launch {
                _retryEvent.emit(Unit)
            }
        }

        private fun setMemberProfile(memberProfile: MemberProfile) {
            _memberProfile.value = memberProfile
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.postValue(state)
        }
    }
