package com.woowacourse.staccato.data.login

import com.woowacourse.staccato.data.ResponseResult

interface LoginDataSource {
    suspend fun requestLoginWithNickname(nickname: String): ResponseResult<String>
}
