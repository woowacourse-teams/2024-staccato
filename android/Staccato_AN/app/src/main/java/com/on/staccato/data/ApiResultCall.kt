package com.on.staccato.data

import com.on.staccato.StaccatoApplication.Companion.retrofit
import com.on.staccato.data.StaccatoClient.getErrorResponse
import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.dto.Status
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

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
                    t: Throwable,
                ) {
                    val networkResult = Exception<T>(t)
                    callback.onResponse(this@ApiResultCall, Response.success(networkResult))
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
    } catch (e: HttpException) {
        ServerError(status = Status.Code(e.code()), message = e.message())
    } catch (e: Throwable) {
        Exception(e, message = e.message.toString())
    }
}
