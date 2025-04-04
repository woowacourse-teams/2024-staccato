package com.on.staccato.presentation.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException2
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.Nickname
import com.on.staccato.domain.model.NicknameState
import com.on.staccato.domain.repository.LoginRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.util.ExceptionState2
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

        val nicknameState: LiveData<NicknameState> = nickname.map { NicknameState.from(it) }

        val nicknameMaxLength = Nickname.MAX_LENGTH

        private val _isLoginSuccess = MutableSingleLiveData(false)
        val isLoginSuccess: SingleLiveData<Boolean>
            get() = _isLoginSuccess

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private val _exception = MutableSingleLiveData<ExceptionState2>()
        val exception: SingleLiveData<ExceptionState2>
            get() = _exception

        fun requestLogin() {
            val nickname = nicknameState.value
            if (nickname is NicknameState.Valid) {
                viewModelScope.launch {
                    repository.loginWithNickname(nickname.value)
                        .onSuccess { updateIsLoginSuccess() }
                        .onServerError(::handleError)
                        .onException2(::handleException)
                }
            }
        }

        private fun updateIsLoginSuccess() {
            _isLoginSuccess.postValue(true)
        }

        private fun handleError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.setValue(state)
        }
    }
