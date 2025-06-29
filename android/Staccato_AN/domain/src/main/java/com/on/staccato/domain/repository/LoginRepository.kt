package com.on.staccato.domain.repository

import com.on.staccato.domain.ApiResult

interface LoginRepository {
    suspend fun loginWithNickname(nickname: String): ApiResult<Unit>

    suspend fun getToken(): Result<String?>
}
