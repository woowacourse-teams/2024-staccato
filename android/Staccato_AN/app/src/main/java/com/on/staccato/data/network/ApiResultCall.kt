package com.on.staccato.data.network

import com.on.staccato.StaccatoApplication.Companion.retrofit
import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.dto.Status
import com.on.staccato.data.network.StaccatoClient.getErrorResponse
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiResultCall<T : Any>(
    private val delegate: Call<T>,
) : Call<ApiResult<T>> {
    override fun enqueue(callback: retrofit2.Callback<ApiResult<T>>) {
        delegate.enqueue(
            object : retrofit2.Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val networkResult: ApiResult<T> = handleApiResponse { response }
                    callback.onResponse(this@ApiResultCall, Response.success(networkResult))
                }

                override fun onFailure(
                    call: Call<T>,
                    throwable: Throwable,
                ) {
                    val exception = handleException<T>(throwable)
                    callback.onResponse(this@ApiResultCall, Response.success(exception))
                }
            },
        )
    }

    override fun execute(): Response<ApiResult<T>> = throw NotImplementedError()

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(delegate.clone())

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}

private const val CREATED = 201
private const val NOT_FOUND_ERROR_BODY = "errorBody를 찾을 수 없습니다."

private fun <T : Any> handleApiResponse(execute: () -> Response<T>): ApiResult<T> {
    return try {
        val response: Response<T> = execute()
        if (response.code() == 413) return ServerError(status = Status.Code(413), message = "10MB 이하의 사진만 업로드할 수 있어요!")
        val body: T? = response.body()

        when {
            response.isSuccessful && response.code() == CREATED -> Success(body as T)
            response.isSuccessful && body != null -> Success(body)
            else -> {
                val errorBody: ResponseBody =
                    response.errorBody()
                        ?: throw IllegalArgumentException(NOT_FOUND_ERROR_BODY)
                val errorResponse: ErrorResponse = retrofit.getErrorResponse(errorBody)
                ServerError(
                    status = Status.Message(errorResponse.status),
                    message = errorResponse.message,
                )
            }
        }
    } catch (httpException: HttpException) {
        ServerError(status = Status.Code(httpException.code()), message = httpException.message())
    } catch (throwable: Throwable) {
        handleException<T>(throwable)
    }
}

private fun <T : Any> handleException(throwable: Throwable) =
    when (throwable) {
        is IOException -> Exception.NetworkError<T>()
        else -> Exception.UnknownError<T>()
    }
