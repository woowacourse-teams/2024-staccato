package com.on.staccato.data

import com.on.staccato.data.dto.Status

suspend fun <T : Any> ResponseResult<T>.onSuccess(executable: suspend (T) -> Unit): ResponseResult<T> =
    apply {
        if (this is Success<T>) {
            executable(data)
        }
    }

suspend fun <T : Any> ResponseResult<T>.onServerError(executable: suspend (status: Status, message: String) -> Unit): ResponseResult<T> =
    apply {
        if (this is ServerError<T>) {
            executable(status, message)
        }
    }

suspend fun <T : Any> ResponseResult<T>.onException(executable: suspend (e: Throwable, message: String) -> Unit): ResponseResult<T> =
    apply {
        if (this is Exception<T>) {
            executable(e, message)
        }
    }
