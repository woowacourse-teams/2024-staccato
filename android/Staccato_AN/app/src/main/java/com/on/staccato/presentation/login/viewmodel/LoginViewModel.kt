package com.on.staccato.presentation.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.StaccatoApplication
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.repository.LoginRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val repository: LoginRepository,
    ) : ViewModel() {
        val nickname = MutableLiveData("")

        private val _isLoginSuccess = MutableSingleLiveData(false)
        val isLoginSuccess: SingleLiveData<Boolean>
            get() = _isLoginSuccess

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        fun requestLogin() {
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
        }

        private fun handleException(
            e: Throwable,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
        }
    }
