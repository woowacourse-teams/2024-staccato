package com.on.staccato.data.login

import com.on.staccato.data.ResponseResult
import com.on.staccato.domain.repository.LoginRepository
import javax.inject.Inject

class LoginDefaultRepository
    @Inject
    constructor(
        private val loginDataSource: LoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithNickname(nickname: String): ResponseResult<String> {
            return when (val result = loginDataSource.requestLoginWithNickname(nickname)) {
                is ResponseResult.Exception -> ResponseResult.Exception(result.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ResponseResult.ServerError -> ResponseResult.ServerError(result.status, result.message)
                is ResponseResult.Success -> ResponseResult.Success(result.data.token)
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 상태가 불안정 합니다."
        }
    }
