package com.woowacourse.staccato.data

import com.woowacourse.staccato.data.dto.Status

sealed interface ResponseResult<T : Any> {
    class Success<T : Any>(val data: T) : ResponseResult<T>

    class ServerError<T : Any>(val status: Status, val message: String) : ResponseResult<T>

    class Exception<T : Any>(val e: Throwable, val message: String) : ResponseResult<T>
}
