package com.on.staccato.data.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class ApiResultCall<T : Any>
    @Inject
    constructor(
        private val delegate: Call<T>,
        private val responseHandler: ResponseHandler,
    ) : Call<ApiResult<T>> {
        override fun enqueue(callback: retrofit2.Callback<ApiResult<T>>) {
            delegate.enqueue(
                object : retrofit2.Callback<T> {
                    override fun onResponse(
                        call: Call<T>,
                        response: Response<T>,
                    ) {
                        val networkResult: ApiResult<T> = responseHandler.handleApiResponse { response }
                        callback.onResponse(this@ApiResultCall, Response.success(networkResult))
                    }

                    override fun onFailure(
                        call: Call<T>,
                        throwable: Throwable,
                    ) {
                        val exception = responseHandler.handleException<T>(throwable)
                        callback.onResponse(this@ApiResultCall, Response.success(exception))
                    }
                },
            )
        }

        override fun execute(): Response<ApiResult<T>> = throw NotImplementedError()

        override fun clone(): Call<ApiResult<T>> = ApiResultCall(delegate.clone(), responseHandler)

        override fun isExecuted(): Boolean = delegate.isExecuted

        override fun cancel() {
            delegate.cancel()
        }

        override fun isCanceled(): Boolean = delegate.isCanceled

        override fun request(): Request = delegate.request()

        override fun timeout(): Timeout = delegate.timeout()
    }
