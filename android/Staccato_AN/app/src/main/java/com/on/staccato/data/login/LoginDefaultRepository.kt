package com.on.staccato.data.login

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.domain.repository.LoginRepository
import javax.inject.Inject

class LoginDefaultRepository
    @Inject
    constructor(
        private val loginDataSource: LoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithNickname(nickname: String): ApiResult<String> {
            return when (val result = loginDataSource.requestLoginWithNickname(nickname)) {
                is Exception -> Exception(result.e)
                is ServerError -> ServerError(result.status, result.message)
                is Success -> Success(result.data.token)
            }
        }
    }
