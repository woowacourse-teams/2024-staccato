package com.on.staccato.data.login

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.dto.login.NicknameLoginRequest
import com.on.staccato.data.dto.login.NicknameLoginResponse

class LoginRemoteDataSource(
    private val loginApiService: LoginApiService = StaccatoClient.loginApiService,
) : LoginDataSource {
    override suspend fun requestLoginWithNickname(nickname: String): ResponseResult<NicknameLoginResponse> =
        handleApiResponse { loginApiService.postNicknameLogin(NicknameLoginRequest(nickname)) }
}
