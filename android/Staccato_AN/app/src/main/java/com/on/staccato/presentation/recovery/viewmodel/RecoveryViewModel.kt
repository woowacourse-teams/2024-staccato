package com.on.staccato.presentation.recovery.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.recovery.RecoveryHandler
import com.on.staccato.presentation.util.ExceptionState
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
        val isRecoverySuccess: SingleLiveData<Boolean>
            get() = _isRecoverySuccess

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

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
                    .onServerError(::handleError)
                    .onException(::handleException)
            }
        }

        private fun updateIsRecoverySuccess() {
            _isRecoverySuccess.postValue(true)
        }

        private fun handleError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState) {
            _errorMessage.postValue(state.message)
        }
    }
