package com.woowacourse.staccato.presentation.login.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.StaccatoApplication
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.repository.LoginRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.login.LoginHandler
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel(), LoginHandler {
    val nickname = MutableLiveData("")

    private val _isLoginSuccess = MutableSingleLiveData(false)
    val isLoginSuccess: SingleLiveData<Boolean>
        get() = _isLoginSuccess

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String>
        get() = _errorMessage

    override fun onStartClicked() {
        val nickname = nickname.value ?: ""
        viewModelScope.launch {
            repository.loginWithNickname(nickname)
                .onSuccess(::saveUserToken)
                .onServerError(::handleError)
                .onException(::handleException)
        }
    }

    private fun saveUserToken(newToken: String) {
        viewModelScope.launch {
            StaccatoApplication.userInfoPrefsManager.setToken(newToken)
            _isLoginSuccess.postValue(true)
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
