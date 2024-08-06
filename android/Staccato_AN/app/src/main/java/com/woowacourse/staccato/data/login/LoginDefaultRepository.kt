package com.woowacourse.staccato.data.login

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.repository.LoginRepository

class LoginDefaultRepository(
    private val loginDataSource: LoginDataSource = LoginRemoteDataSource(),
) : LoginRepository {
    override suspend fun loginWithNickname(nickname: String): ResponseResult<String> {
        return when (val result = loginDataSource.requestLoginWithNickname(nickname)) {
            is ResponseResult.Exception -> ResponseResult.Exception(result.e, EXCEPTION_ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(result.code, result.message)
            is ResponseResult.Success -> ResponseResult.Success(result.data.token)
        }
    }

    companion object {
        private const val EXCEPTION_ERROR_MESSAGE = "예기치 못한 에러가 발생했습니다. 다시 시도해주세요."
    }
}
