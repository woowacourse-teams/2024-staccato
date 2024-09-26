package com.on.staccato.presentation.recovery.viewmodel

import android.util.Log
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
import kotlinx.coroutines.launch

class RecoveryViewModel(private val repository: MemberRepository) : ViewModel(), RecoveryHandler {
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
        when (status) {
            is Status.Message ->
                Log.e(
                    this::class.java.simpleName,
                    "Error Occurred | status: ${status.message}, message: $errorMessage",
                )

            is Status.Code ->
                Log.e(
                    this::class.java.simpleName,
                    "Error Occurred | status: ${status.code}, message: $errorMessage",
                )
        }
    }

    private fun handleException(
        e: Throwable,
        errorMessage: String,
    ) {
        _errorMessage.postValue(errorMessage)
        Log.e(
            this::class.java.simpleName,
            "Exception Caught | throwable: $e, message: $errorMessage",
        )
    }
}
