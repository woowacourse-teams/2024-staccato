package com.on.staccato.data.network

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import javax.inject.Inject

class ApiResultCallAdapter
    @Inject
    constructor(
        private val resultType: Type,
        private val responseHandler: ResponseHandler,
    ) : CallAdapter<Type, Call<ApiResult<Type>>> {
        override fun responseType(): Type = resultType

        override fun adapt(call: Call<Type>): Call<ApiResult<Type>> = ApiResultCall(call, responseHandler)
    }
