package com.on.staccato.data.member

import com.on.staccato.data.dto.member.RecoveryCodeResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface MemberApiService {
    @POST(MEMBERS_PATH)
    suspend fun postRecoveryCode(
        @Query(RECOVERY_CODE) recoveryCode: String,
    ): Response<RecoveryCodeResponse>

    companion object {
        private const val MEMBERS_PATH = "/members"
        private const val RECOVERY_CODE = "code"
    }
}
