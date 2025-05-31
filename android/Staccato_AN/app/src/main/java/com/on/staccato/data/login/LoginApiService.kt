package com.on.staccato.data.login

import com.on.staccato.data.dto.login.NicknameLoginRequest
import com.on.staccato.data.dto.login.NicknameLoginResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST(LOGIN_PATH_V2)
    suspend fun postNicknameLogin(
        @Body nicknameLoginRequest: NicknameLoginRequest,
    ): ApiResult<NicknameLoginResponse>

    companion object {
        private const val LOGIN_PATH = "/login"
        private const val LOGIN_PATH_V2 = "/v2${LOGIN_PATH}"
    }
}
