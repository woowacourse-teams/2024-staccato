package com.on.staccato.data.login

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.login.NicknameLoginRequest
import com.on.staccato.data.dto.login.NicknameLoginResponse
import javax.inject.Inject

class LoginRemoteDataSource
    @Inject
    constructor(
        private val loginApiService: LoginApiService,
    ) : LoginDataSource {
        override suspend fun requestLoginWithNickname(nickname: String): ApiResult<NicknameLoginResponse> =
            loginApiService.postNicknameLogin(NicknameLoginRequest(nickname))
    }
