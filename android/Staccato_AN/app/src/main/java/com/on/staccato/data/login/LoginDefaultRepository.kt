package com.on.staccato.data.login

import com.on.staccato.StaccatoApplication
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.repository.LoginRepository
import javax.inject.Inject

class LoginDefaultRepository
    @Inject
    constructor(
        private val loginDataSource: LoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithNickname(nickname: String): ApiResult<Unit> =
            loginDataSource.requestLoginWithNickname(nickname).handle {
                StaccatoApplication.userInfoPrefsManager.setToken(it.token)
            }

        override suspend fun getToken(): Result<String?> =
            runCatching {
                StaccatoApplication.userInfoPrefsManager.getToken()
            }
    }
