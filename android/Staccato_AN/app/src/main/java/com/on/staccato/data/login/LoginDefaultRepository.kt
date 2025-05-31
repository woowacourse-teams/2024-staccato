package com.on.staccato.data.login

import com.on.staccato.data.member.MemberDataSource
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.repository.LoginRepository
import javax.inject.Inject

class LoginDefaultRepository
    @Inject
    constructor(
        private val memberDataSource: MemberDataSource,
        private val loginDataSource: LoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithNickname(nickname: String): ApiResult<Unit> =
            loginDataSource.requestLoginWithNickname(nickname).handle {
                memberDataSource.setTokenAndId(it.token, it.id)
            }

        override suspend fun getToken(): Result<String?> =
            runCatching {
                memberDataSource.getToken()
            }
    }
