package com.on.staccato.data.member

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ResponseResult.Exception
import com.on.staccato.data.ResponseResult.ServerError
import com.on.staccato.data.ResponseResult.Success
import com.on.staccato.data.StaccatoClient
import com.on.staccato.domain.repository.MemberRepository

class MemberDefaultRepository(
    private val memberApiService: MemberApiService = StaccatoClient.memberApiService,
) : MemberRepository {
    override suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ResponseResult<String> {
        val responseResult = handleApiResponse { memberApiService.postRecoveryCode(recoveryCode) }
        return when (responseResult) {
            is Exception -> Exception(responseResult.e, EXCEPTION_ERROR_MESSAGE)
            is ServerError -> ServerError(responseResult.status, responseResult.message)
            is Success -> Success(responseResult.data.token)
        }
    }

    companion object {
        private const val EXCEPTION_ERROR_MESSAGE = "예기치 못한 에러가 발생했습니다. 다시 시도해주세요."
    }
}
