package com.woowacourse.staccato.data.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/login")
    suspend fun postNicknameLogin(
        @Body nickname: String,
    ): Response<String>
}
