package com.woowacourse.staccato.data

import com.woowacourse.staccato.StaccatoApplication
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val token = StaccatoApplication.userInfoPrefsManager.getToken()
            val request =
                if (!token.isNullOrEmpty()) {
                    chain.request().addTokenHeader(token)
                } else {
                    chain.request()
                }
            chain.proceed(request)
        }
    }

    private fun Request.addTokenHeader(token: String): Request =
        this.newBuilder()
            .addHeader(AUTHORIZATION_HEADER, token)
            .build()

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
