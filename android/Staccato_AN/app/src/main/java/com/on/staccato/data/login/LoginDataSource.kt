package com.on.staccato.data.login

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.login.NicknameLoginResponse

interface LoginDataSource {
    suspend fun requestLoginWithNickname(nickname: String): ResponseResult<NicknameLoginResponse>
}
