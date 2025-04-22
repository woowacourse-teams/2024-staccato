package com.on.staccato.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getToken() }
        val request =
            if (!token.isNullOrEmpty()) {
                chain.request().addTokenHeader(token)
            } else {
                chain.request()
            }
        return chain.proceed(request)
    }

    private fun Request.addTokenHeader(token: String): Request =
        this.newBuilder()
            .addHeader(AUTHORIZATION_HEADER, token)
            .build()

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
