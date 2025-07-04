package com.on.staccato.data.network

import com.on.staccato.data.member.MemberDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenManager
    @Inject
    constructor(
        private val memberLocalDataSource: MemberDataSource,
    ) {
        private var cachedToken: String? = null

        suspend fun getToken(): String? {
            return if (cachedToken.isNullOrEmpty()) {
                fetchAndCacheToken()
            } else {
                cachedToken
            }
        }

        private suspend fun fetchAndCacheToken(): String? {
            val token =
                withContext(Dispatchers.IO) {
                    memberLocalDataSource.getToken()
                }
            cachedToken = token
            return token
        }

        fun clearToken() {
            cachedToken = null
        }
    }
