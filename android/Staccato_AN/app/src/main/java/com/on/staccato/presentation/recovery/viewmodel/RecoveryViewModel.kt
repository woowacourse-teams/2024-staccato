package com.on.staccato.presentation.recovery.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.StaccatoApplication
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.recovery.RecoveryHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoveryViewModel
    @Inject
    constructor(
        private val repository: MemberRepository,
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

        private fun requestRecovery() {
            val code = recoveryCode.value ?: ""
            viewModelScope.launch {
                repository.fetchTokenWithRecoveryCode(code)
                    .onSuccess(::saveUserToken)
                    .onServerError(::handleError)
                    .onException(::handleException)
            }
        }

        private fun saveUserToken(newToken: String) {
            viewModelScope.launch {
                StaccatoApplication.userInfoPrefsManager.setToken(newToken)
                _isRecoverySuccess.postValue(true)
            }
        }

        private fun handleError(
            status: Status,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(
            e: Throwable,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
        }
    }
