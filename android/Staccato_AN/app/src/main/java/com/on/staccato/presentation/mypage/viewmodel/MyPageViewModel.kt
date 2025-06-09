package com.on.staccato.presentation.mypage.viewmodel

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
import com.on.staccato.presentation.mypage.MemberProfileHandler
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        private val myPageRepository: MyPageRepository,
        private val notificationRepository: NotificationRepository,
    ) : ViewModel(), MemberProfileHandler {
        var hasTimelineUpdated = false
            private set

        var hasProfileUpdated = false
            private set

        private val _memberProfile = MutableLiveData<MemberProfile>()
        val memberProfile: LiveData<MemberProfile>
            get() = _memberProfile

        private val _uuidCode = MutableSingleLiveData<String>()
        val uuidCode: SingleLiveData<String>
            get() = _uuidCode

        private val _hasNotification = MutableStateFlow(false)
        val hasNotification: StateFlow<Boolean> = _hasNotification.asStateFlow()

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _exceptionState = MutableSingleLiveData<ExceptionState2>()
        val exceptionState: SingleLiveData<ExceptionState2>
            get() = _exceptionState

        override fun onCodeCopyClicked() {
            val memberProfile = memberProfile.value
            if (memberProfile != null) {
                _uuidCode.setValue(memberProfile.uuidCode)
            } else {
                _errorMessage.setValue(ERROR_NO_MEMBER_PROFILE)
            }
        }

        fun changeProfileImage(multipart: MultipartBody.Part) {
            viewModelScope.launch {
                myPageRepository.changeProfileImage(multipart)
                    .onSuccess {
                        _memberProfile.value = memberProfile.value?.copy(profileImageUrl = it)
                        hasProfileUpdated = true
                    }.onServerError(::handleError)
                    .onException2(::handleException2)
            }
        }

        fun fetchMemberProfile() {
            viewModelScope.launch {
                myPageRepository.getMemberProfile()
                    .onSuccess { _memberProfile.value = it }
                    .onServerError(::handleError)
                    .onException2(::handleException2)
            }
        }

        fun fetchNotificationExistence() {
            viewModelScope.launch {
                notificationRepository.getNotificationExistence()
                    .onSuccess { _hasNotification.value = it.isExist }
                    .onServerError(::handleError)
                    .onException2(::handleException2)
            }
        }

        fun updateHasTimelineUpdated() {
            hasTimelineUpdated = true
        }

        private fun handleError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException2(state: ExceptionState2) {
            _exceptionState.postValue(state)
        }

        companion object {
            private const val ERROR_NO_MEMBER_PROFILE = "프로필 정보를 조회할 수 없습니다.\n잠시 후에 다시 시도해주세요."
        }
    }
