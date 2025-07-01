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
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
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
        private val loginRepository: LoginRepository,
        private val notificationRepository: NotificationRepository,
    ) : ViewModel() {
        val nickname = MutableLiveData("")

        val nicknameState: LiveData<NicknameState> = nickname.map { NicknameState.from(it) }

        val nicknameMaxLength = Nickname.MAX_LENGTH

        private var token: String? = null

        private val _isLoggedIn = MutableSingleLiveData(false)
        val isLoggedIn: SingleLiveData<Boolean> = _isLoggedIn

        private val _isLoginSuccess = MutableSingleLiveData(false)
        val isLoginSuccess: SingleLiveData<Boolean> get() = _isLoginSuccess

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        fun requestLogin() {
            val nickname = nicknameState.value
            if (nickname is NicknameState.Valid) {
                viewModelScope.launch {
                    loginRepository.loginWithNickname(nickname.value)
                        .onSuccess { updateIsLoginSuccess() }
                        .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                        .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
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
                loginRepository.getToken()
                    .onSuccess { token = it }
                    .onFailure { emitMessageEvent(MessageEvent.from(ExceptionType.UNKNOWN)) }
                _isLoggedIn.setValue(!token.isNullOrEmpty())
            }
        }

        private fun updateIsLoginSuccess() {
            _isLoginSuccess.postValue(true)
        }

        private fun emitMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
