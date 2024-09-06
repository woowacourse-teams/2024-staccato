package com.on.staccato.data.login

import com.on.staccato.data.dto.login.NicknameLoginRequest
import com.on.staccato.data.dto.login.NicknameLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/login")
    suspend fun postNicknameLogin(
        @Body nicknameLoginRequest: NicknameLoginRequest,
    ): Response<NicknameLoginResponse>
}
