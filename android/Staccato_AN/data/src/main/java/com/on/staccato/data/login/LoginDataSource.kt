package com.on.staccato.data.login

import com.on.staccato.data.dto.login.NicknameLoginResponse
import com.on.staccato.domain.ApiResult

interface LoginDataSource {
    suspend fun requestLoginWithNickname(nickname: String): ApiResult<NicknameLoginResponse>
}
