package com.on.staccato.data.member

import com.on.staccato.data.dto.member.MemberSearchResponse
import com.on.staccato.data.dto.member.RecoveryCodeResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MemberApiService {
    @POST(MEMBERS_PATH_V2)
    suspend fun postRecoveryCode(
        @Query(RECOVERY_CODE) recoveryCode: String,
    ): ApiResult<RecoveryCodeResponse>

    @GET(MEMBERS_SEARCH_PATH)
    suspend fun getMembersBy(
        @Query(NICKNAME) nickname: String,
    ): ApiResult<MemberSearchResponse>

    companion object {
        private const val MEMBERS_PATH = "/members"
        private const val MEMBERS_PATH_V2 = "/v2$MEMBERS_PATH"
        private const val RECOVERY_CODE = "code"
        private const val NICKNAME = "nickname"
        private const val MEMBERS_SEARCH_PATH = "$MEMBERS_PATH/search"
    }
}
