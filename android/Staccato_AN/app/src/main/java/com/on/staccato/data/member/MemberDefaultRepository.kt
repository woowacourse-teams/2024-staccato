package com.on.staccato.data.member

import com.on.staccato.StaccatoApplication
import com.on.staccato.data.ApiResult
import com.on.staccato.data.handle
import com.on.staccato.domain.repository.MemberRepository
import javax.inject.Inject

class MemberDefaultRepository
    @Inject
    constructor(
        private val memberApiService: MemberApiService,
    ) : MemberRepository {
        override suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ApiResult<Unit> =
            memberApiService.postRecoveryCode(recoveryCode).handle { StaccatoApplication.userInfoPrefsManager.setToken(it.token) }
    }
