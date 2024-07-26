package com.woowacourse.staccato.data

import retrofit2.HttpException
import retrofit2.Response

object ApiResponseHandler {
    suspend fun <T : Any> handleApiResponse(execute: suspend () -> Response<T>): ResponseResult<T> {
        return try {
            val response = execute()
            val body = response.body()

            when {
                response.isSuccessful && response.code() == 201 -> {
                    val locationHeader = response.headers()["Location"].toString()
                    ResponseResult.Success(locationHeader as T)
                }
                response.isSuccessful && body != null -> ResponseResult.Success(body)
                response.isSuccessful && response.code() == 204 -> ResponseResult.Success(Unit as T)
                else ->
                    ResponseResult.ServerError(
                        code = response.code(),
                        message = response.message(),
                    )
            }
        } catch (e: HttpException) {
            ResponseResult.ServerError(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            ResponseResult.Exception(e, message = e.message.toString())
        }
    }

    suspend fun <T : Any> ResponseResult<T>.onSuccess(executable: suspend (T) -> Unit): ResponseResult<T> =
        apply {
            if (this is ResponseResult.Success<T>) {
                executable(data)
            }
        }

    suspend fun <T : Any> ResponseResult<T>.onServerError(executable: suspend (code: Int, message: String) -> Unit): ResponseResult<T> =
        apply {
            if (this is ResponseResult.ServerError<T>) {
                executable(code, message)
            }
        }

    suspend fun <T : Any> ResponseResult<T>.onException(executable: suspend (e: Throwable, message: String) -> Unit): ResponseResult<T> =
        apply {
            if (this is ResponseResult.Exception<T>) {
                executable(e, message)
            }
        }
}
