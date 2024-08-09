package com.woowacourse.staccato.data.login

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.login.NicknameLoginResponse

interface LoginDataSource {
    suspend fun requestLoginWithNickname(nickname: String): ResponseResult<NicknameLoginResponse>
}
