package com.on.staccato.data.login

import com.on.staccato.data.ApiResult
import com.on.staccato.data.handle
import com.on.staccato.domain.repository.LoginRepository
import javax.inject.Inject

class LoginDefaultRepository
    @Inject
    constructor(
        private val loginDataSource: LoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithNickname(nickname: String): ApiResult<String> =
            loginDataSource.requestLoginWithNickname(nickname).handle { it.token }
    }
