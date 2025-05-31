package com.on.staccato.data.member

import com.on.staccato.StaccatoApplication
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.Members
import com.on.staccato.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MemberDefaultRepository
    @Inject
    constructor(
        private val memberApiService: MemberApiService,
    ) : MemberRepository {
        override suspend fun fetchTokenWithRecoveryCode(recoveryCode: String): ApiResult<Unit> =
            memberApiService.postRecoveryCode(recoveryCode)
                .handle { StaccatoApplication.userInfoPrefsManager.setTokenAndId(it.token, it.id) }

        override suspend fun getMemberId(): Result<Long> =
            runCatching {
                StaccatoApplication.userInfoPrefsManager.getMemberId()
            }

        override suspend fun searchMembersBy(nickname: String): Flow<ApiResult<Members>> =
            flow {
                emit(
                    memberApiService.getMembersBy(nickname)
                        .handle { Members(it.members.map { it.toDomain() }) },
                )
            }
    }
