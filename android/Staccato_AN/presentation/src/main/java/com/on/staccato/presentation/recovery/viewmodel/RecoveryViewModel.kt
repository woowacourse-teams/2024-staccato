package com.on.staccato.presentation.recovery.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.recovery.RecoveryHandler
import com.on.staccato.util.launchOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoveryViewModel
    @Inject
    constructor(
        private val repository: MemberRepository,
        private val notificationRepository: NotificationRepository,
    ) : ViewModel(), RecoveryHandler {
        val recoveryCode = MutableLiveData("")

        private val _isRecoverySuccess = MutableSingleLiveData(false)
        val isRecoverySuccess: SingleLiveData<Boolean> get() = _isRecoverySuccess

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        override fun onRecoveryClicked() {
            requestRecovery()
        }

        fun registerCurrentFcmToken() {
            CoroutineScope(SupervisorJob()).launchOnce {
                notificationRepository.registerCurrentFcmToken()
            }
        }

        private fun requestRecovery() {
            val code = recoveryCode.value ?: ""
            viewModelScope.launch {
                repository.fetchTokenWithRecoveryCode(code)
                    .onSuccess { updateIsRecoverySuccess() }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        private fun updateIsRecoverySuccess() {
            _isRecoverySuccess.postValue(true)
        }

        private fun emitMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
