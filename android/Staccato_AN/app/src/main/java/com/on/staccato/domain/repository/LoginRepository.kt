package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult

interface LoginRepository {
    suspend fun loginWithNickname(nickname: String): ResponseResult<String>
}
