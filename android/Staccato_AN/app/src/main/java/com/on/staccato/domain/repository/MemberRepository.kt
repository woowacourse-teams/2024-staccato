package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult

interface MemberRepository {
    suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ApiResult<Unit>
}
