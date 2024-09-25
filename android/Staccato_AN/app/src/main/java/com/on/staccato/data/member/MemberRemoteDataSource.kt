package com.on.staccato.data.member

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.dto.member.RecoveryCodeResponse

class MemberRemoteDataSource (
    private val memberApiService: MemberApiService = StaccatoClient.memberApiService,
): MemberDataSource {
    override suspend fun postRecoveryCode(recoveryCode: String): ResponseResult<RecoveryCodeResponse> =
        handleApiResponse { memberApiService.postRecoveryCode(recoveryCode) }
}
