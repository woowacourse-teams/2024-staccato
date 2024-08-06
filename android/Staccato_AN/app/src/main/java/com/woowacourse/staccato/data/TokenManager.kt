package com.woowacourse.staccato.data

import com.woowacourse.staccato.StaccatoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenManager {
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
                StaccatoApplication.userInfoPrefsManager.getToken()
            }
        cachedToken = token
        return token
    }

    fun clearToken() {
        cachedToken = null
    }
}
