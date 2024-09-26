package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult

interface MemberRepository {
    suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ResponseResult<String>
}
