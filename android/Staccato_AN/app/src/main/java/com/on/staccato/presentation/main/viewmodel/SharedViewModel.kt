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
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.main.BottomSheetState
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
    @Inject
    constructor(private val myPageRepository: MyPageRepository) : ViewModel() {
        private val _isHalfModeRequested = MutableLiveData<Boolean>(false)
        val isHalfModeRequested: LiveData<Boolean> get() = _isHalfModeRequested

        private val _firstVisibleCategoryIndex = MutableStateFlow<Int>(0)
        val firstVisibleCategoryIndex: StateFlow<Int> get() = _firstVisibleCategoryIndex.asStateFlow()

        private val _recentFirstVisibleCategoryIndex = MutableLiveData<Int>()
        val recentFirstVisibleCategoryIndex: LiveData<Int> get() = _recentFirstVisibleCategoryIndex

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

        private val _bottomSheetState = MutableLiveData<BottomSheetState>(BottomSheetState.HALF_EXPANDED)
        val bottomSheetState: LiveData<BottomSheetState> get() = _bottomSheetState

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _staccatoLocation = MutableLiveData<LocationUiModel>()
        val staccatoLocation: LiveData<LocationUiModel> get() = _staccatoLocation

        private val _exception = MutableLiveData<ExceptionState2>()
        val exception: LiveData<ExceptionState2> get() = _exception

        private val _isRetry = MutableLiveData<Boolean>()
        val isRetry: LiveData<Boolean> get() = _isRetry

        private val isDragging = MutableLiveData<Boolean>(false)

        fun fetchMemberProfile() {
            viewModelScope.launch {
                val result = myPageRepository.getMemberProfile()
                result.onException2(::handleException)
                    .onServerError(::handleServerError)
                    .onSuccess(::setMemberProfile)
            }
        }

        fun updateFirstVisibleCategoryIndex(value: Int) {
            _firstVisibleCategoryIndex.value = value
        }

        fun updateRecentFirstVisibleCategoryIndex() {
            _recentFirstVisibleCategoryIndex.value = _firstVisibleCategoryIndex.value
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

        fun updateBottomSheet(state: BottomSheetState) {
            val isDifferent = state != _bottomSheetState.value
            val isExpanded = state == BottomSheetState.EXPANDED
            val isHalfExpanded = state == BottomSheetState.HALF_EXPANDED

            when {
                isDifferent && isDragging.value == false -> _bottomSheetState.value = state
                isDifferent && isExpanded -> _bottomSheetState.value = state
                isHalfExpanded -> updateIsHalfModeRequested(false)
            }
        }

        fun updateIsHalfModeRequested(state: Boolean) {
            _isHalfModeRequested.value = state
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
            _isRetry.value = true
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
