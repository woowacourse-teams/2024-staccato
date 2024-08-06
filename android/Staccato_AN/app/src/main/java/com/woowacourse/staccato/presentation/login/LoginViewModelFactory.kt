package com.woowacourse.staccato.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.login.LoginDefaultRepository
import com.woowacourse.staccato.domain.repository.LoginRepository

class LoginViewModelFactory(
    private val repository: LoginRepository = LoginDefaultRepository(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        } else {
            throw IllegalArgumentException("확인되지 않은 ViewModel 클래스입니다.")
        }
    }
}
