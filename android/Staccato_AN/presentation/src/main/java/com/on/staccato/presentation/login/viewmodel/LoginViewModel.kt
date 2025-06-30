package com.on.staccato.presentation.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.model.Nickname
import com.on.staccato.domain.model.NicknameState
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.LoginRepository
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.toMessageId
import com.on.staccato.util.launchOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val repository: LoginRepository,
        private val notificationRepository: NotificationRepository,
    ) : ViewModel() {
        val nickname = MutableLiveData("")

        val nicknameState: LiveData<NicknameState> = nickname.map { NicknameState.from(it) }

        val nicknameMaxLength = Nickname.MAX_LENGTH

        private val token: MutableLiveData<String?> = MutableLiveData(null)

        private val _isLoggedIn = MutableSingleLiveData(false)
        val isLoggedIn: SingleLiveData<Boolean> = _isLoggedIn

        private val _isLoginSuccess = MutableSingleLiveData(false)
        val isLoginSuccess: SingleLiveData<Boolean>
            get() = _isLoginSuccess

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionMessage = MutableSingleLiveData<Int>()
        val exceptionMessage: SingleLiveData<Int>
            get() = _exceptionMessage

        fun requestLogin() {
            val nickname = nicknameState.value
            if (nickname is NicknameState.Valid) {
                viewModelScope.launch {
                    repository.loginWithNickname(nickname.value)
                        .onSuccess { updateIsLoginSuccess() }
                        .onServerError(::handleError)
                        .onException(::handleException)
                }
            }
        }

        fun registerCurrentFcmToken() {
            CoroutineScope(SupervisorJob()).launchOnce {
                notificationRepository.registerCurrentFcmToken()
            }
        }

        fun fetchToken() {
            viewModelScope.launch {
                repository.getToken()
                    .onSuccess { token.value = it }
                    .onFailure {
                        handleException(ExceptionType.UNKNOWN)
                    }
                _isLoggedIn.setValue(!token.value.isNullOrEmpty())
            }
        }

        private fun updateIsLoginSuccess() {
            _isLoginSuccess.postValue(true)
        }

        private fun handleError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionType) {
            _exceptionMessage.postValue(state.toMessageId())
        }
    }
