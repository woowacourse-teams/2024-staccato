package com.on.staccato.data.login

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.login.NicknameLoginResponse

interface LoginDataSource {
    suspend fun requestLoginWithNickname(nickname: String): ApiResult<NicknameLoginResponse>
}
