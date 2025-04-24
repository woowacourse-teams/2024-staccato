package com.on.staccato.data.network

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResultCallAdapter(
    private val resultType: Type,
) : CallAdapter<Type, Call<ApiResult<Type>>> {
    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Type>): Call<ApiResult<Type>> = ApiResultCall(call)
}
