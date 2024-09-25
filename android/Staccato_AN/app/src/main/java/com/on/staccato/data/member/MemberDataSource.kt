package com.on.staccato.data.member

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.member.RecoveryCodeResponse

interface MemberDataSource {
    suspend fun postRecoveryCode(recoveryCode: String): ResponseResult<RecoveryCodeResponse>
}
