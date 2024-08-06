package com.woowacourse.staccato.data.login

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.dto.login.NicknameLoginRequest
import com.woowacourse.staccato.data.dto.login.NicknameLoginResponse

class LoginRemoteDataSource(
    private val loginApiService: LoginApiService = StaccatoClient.loginApiService,
) : LoginDataSource {
    override suspend fun requestLoginWithNickname(nickname: String): ResponseResult<NicknameLoginResponse> =
        handleApiResponse { loginApiService.postNicknameLogin(NicknameLoginRequest(nickname)) }
}
