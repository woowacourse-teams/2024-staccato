package com.on.staccato.data.member

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ResponseResult.Exception
import com.on.staccato.data.ResponseResult.ServerError
import com.on.staccato.data.ResponseResult.Success
import com.on.staccato.domain.repository.MemberRepository
import javax.inject.Inject

class MemberDefaultRepository
    @Inject
    constructor(
        private val memberApiService: MemberApiService,
    ) : MemberRepository {
        override suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ResponseResult<String> {
            val responseResult = handleApiResponse { memberApiService.postRecoveryCode(recoveryCode) }
            return when (responseResult) {
                is Exception -> Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data.token)
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
