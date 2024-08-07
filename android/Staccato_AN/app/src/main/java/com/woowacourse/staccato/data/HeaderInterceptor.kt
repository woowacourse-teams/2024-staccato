package com.woowacourse.staccato.data

import com.woowacourse.staccato.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val headerValue: String = DEFAULT_VALUE) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request()
                .newBuilder()
                .addHeader(AUTHORIZATION_HEADER, headerValue)
                .build()
        return chain.proceed(request)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val DEFAULT_VALUE = BuildConfig.TOKEN
    }
}
