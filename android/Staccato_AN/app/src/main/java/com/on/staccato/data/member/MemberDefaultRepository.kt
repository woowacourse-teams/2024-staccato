package com.on.staccato.data.member

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
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
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data.token)
            }
        }
    }
