package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult

interface LoginRepository {
    suspend fun loginWithNickname(nickname: String): ApiResult<String>
}
