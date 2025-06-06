package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.Members
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ApiResult<Unit>

    suspend fun getMemberId(): Result<Long>

    suspend fun getNickname(): Result<String>

    suspend fun searchMembersBy(nickname: String): Flow<ApiResult<Members>>
}
