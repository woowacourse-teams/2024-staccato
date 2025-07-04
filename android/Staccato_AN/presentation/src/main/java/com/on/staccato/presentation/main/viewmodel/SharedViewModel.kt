package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.MyPageRepository
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.category.model.CategoryRefresh
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.main.HomeRefresh
import com.on.staccato.presentation.map.model.LocationUiModel
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

        private val _homeRefresh = MutableLiveData<HomeRefresh>(HomeRefresh.None)
        val homeRefresh: LiveData<HomeRefresh> get() = _homeRefresh

        private val _categoryRefresh = MutableSingleLiveData<CategoryRefresh>(CategoryRefresh.None)
        val categoryRefresh: SingleLiveData<CategoryRefresh> get() = _categoryRefresh

        private val _isPermissionCanceled = MutableLiveData(false)
        val isPermissionCanceled: LiveData<Boolean> get() = _isPermissionCanceled

        private val _isSettingClicked = MutableLiveData(false)
        val isSettingClicked: LiveData<Boolean> get() = _isSettingClicked

        private val _isBottomSheetExpanded = MutableLiveData<Boolean>(false)
        val isBottomSheetExpanded: LiveData<Boolean> get() = _isBottomSheetExpanded

        private val _isBottomSheetHalfExpanded = MutableLiveData(true)
        val isBottomSheetHalfExpanded: LiveData<Boolean> get() = _isBottomSheetHalfExpanded

        private val _staccatoLocation = MutableLiveData<LocationUiModel>()
        val staccatoLocation: LiveData<LocationUiModel> get() = _staccatoLocation

        private val _retryEvent = MutableSharedFlow<Unit>()
        val retryEvent: SharedFlow<Unit> = _retryEvent.asSharedFlow()

        private val isDragging = MutableLiveData<Boolean>(false)

        private val _hasNotification = MutableLiveData<Boolean>(false)
        val hasNotification: LiveData<Boolean> get() = _hasNotification

        private val _currentLocationEvent = MutableSharedFlow<Unit>()
        val currentLocationEvent: SharedFlow<Unit> get() = _currentLocationEvent.asSharedFlow()

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        fun updateCurrentLocationEvent() {
            viewModelScope.launch {
                _currentLocationEvent.emit(Unit)
            }
        }

        fun fetchMemberProfile() {
            viewModelScope.launch {
                myPageRepository.getMemberProfile()
                    .onSuccess(::setMemberProfile)
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun fetchNotificationExistence() {
            viewModelScope.launch {
                notificationRepository.getNotificationExistence()
                    .onSuccess { _hasNotification.value = it.isExist }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
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

        fun updateHomeRefresh(state: HomeRefresh) {
            _homeRefresh.value = state
        }

        fun updateCategoryRefresh(state: CategoryRefresh) {
            _categoryRefresh.setValue(state)
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

        fun updateStaccatoLocation(
            latitude: Double,
            longitude: Double,
        ) {
            _staccatoLocation.value = LocationUiModel(latitude, longitude)
        }

        fun updateIsDragging(state: Boolean) {
            isDragging.value = state
        }

        fun emitMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }

        fun updateIsRetry() {
            viewModelScope.launch {
                _retryEvent.emit(Unit)
            }
        }

        private fun setMemberProfile(memberProfile: MemberProfile) {
            _memberProfile.value = memberProfile
        }
    }
