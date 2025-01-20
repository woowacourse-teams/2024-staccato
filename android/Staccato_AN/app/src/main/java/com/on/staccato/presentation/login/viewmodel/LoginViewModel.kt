package com.on.staccato.presentation.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.dto.Status
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.repository.LoginRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.util.ExceptionState
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
                    .onSuccess { updateIsLoginSuccess() }
                    .onServerError(::handleError)
                    .onException(::handleException)
            }
        }

        private fun updateIsLoginSuccess() {
            _isLoginSuccess.postValue(true)
        }

        private fun handleError(
            status: Status,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState) {
            _errorMessage.postValue(state.message)
        }
    }
