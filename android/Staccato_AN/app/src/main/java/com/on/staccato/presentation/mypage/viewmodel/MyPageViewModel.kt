package com.on.staccato.presentation.mypage.viewmodel

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
import com.on.staccato.presentation.mypage.MemberProfileHandler
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(private val repository: MyPageRepository) :
    ViewModel(), MemberProfileHandler {
        private val _memberProfile = MutableLiveData<MemberProfile>()
        val memberProfile: LiveData<MemberProfile>
            get() = _memberProfile

        private val _uuidCode = MutableSingleLiveData<String>()
        val uuidCode: SingleLiveData<String>
            get() = _uuidCode

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _exception = MutableSingleLiveData<ExceptionState2>()
        val exception: SingleLiveData<ExceptionState2>
            get() = _exception

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
                repository.changeProfileImage(multipart)
                    .onSuccess {
                        _memberProfile.value =
                            memberProfile.value?.copy(profileImageUrl = it)
                    }.onException2(::handleException)
                    .onServerError(::handleError)
            }
        }

        fun fetchMemberProfile() {
            viewModelScope.launch {
                repository.getMemberProfile()
                    .onServerError(::handleError)
                    .onException2(::handleException)
                    .onSuccess {
                        _memberProfile.value = it
                    }
            }
        }

        private fun handleError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.setValue(state)
        }

        companion object {
            private const val ERROR_NO_MEMBER_PROFILE = "프로필 정보를 조회할 수 없습니다.\n잠시 후에 다시 시도해주세요."
        }
    }
