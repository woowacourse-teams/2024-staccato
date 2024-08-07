package com.woowacourse.staccato.data

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
        const val DEFAULT_VALUE =
            "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwibmlja25hbWUiOnsibmlja25hb" +
                "WUiOiJzdGFjY2F0byJ9LCJjcmVhdGVkQXQiOiIyMDI0LTA4LTA2VDE0OjUwOjMwLjI2MDI2NzMwM" +
                "CJ9.UI2PJ1xjSM5ySG8q1KuDj_mPb9Xv2KyGbc2krPCKjFU"
    }
}
