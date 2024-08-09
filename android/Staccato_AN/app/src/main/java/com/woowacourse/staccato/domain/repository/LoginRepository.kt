package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult

interface LoginRepository {
    suspend fun loginWithNickname(nickname: String): ResponseResult<String>
}
