package com.woowacourse.staccato.data.login

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.StaccatoClient

class LoginRemoteDataSource(
    private val loginApiService: LoginApiService = StaccatoClient.loginApiService,
) : LoginDataSource {
    override suspend fun requestLoginWithNickname(nickname: String): ResponseResult<String> =
        handleApiResponse { loginApiService.postNicknameLogin(nickname) }
}
