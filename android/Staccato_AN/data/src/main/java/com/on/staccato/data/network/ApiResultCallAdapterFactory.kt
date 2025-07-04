package com.on.staccato.data.network

import com.on.staccato.domain.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class ApiResultCallAdapterFactory
    @Inject
    constructor(private val responseHandler: ResponseHandler) : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): CallAdapter<*, *>? {
            if (getRawType(returnType) != Call::class.java) {
                return null
            }

            val responseType = getParameterUpperBound(0, returnType as ParameterizedType)

            if (getRawType(responseType) != ApiResult::class.java) {
                return null
            }

            val resultType = getParameterUpperBound(0, responseType as ParameterizedType)
            return ApiResultCallAdapter(resultType, responseHandler)
        }
    }
