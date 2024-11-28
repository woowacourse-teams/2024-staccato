package com.on.staccato.data

import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.dto.Status
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

class NetworkResultCall<T : Any>(
    private val proxy: Call<T>,
) : Call<ResponseResult<T>> {
    override fun enqueue(callback: retrofit2.Callback<ResponseResult<T>>) {
        proxy.enqueue(
            object : retrofit2.Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val networkResult = handleApiResponse2 { response }
                    callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    val networkResult = Exception<T>(t)
                    callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
                }
            },
        )
    }

    override fun execute(): Response<ResponseResult<T>> = throw NotImplementedError()

    override fun clone(): Call<ResponseResult<T>> = NetworkResultCall(proxy.clone())

    override fun isExecuted(): Boolean = proxy.isExecuted

    override fun cancel() {
        proxy.cancel()
    }

    override fun isCanceled(): Boolean = proxy.isCanceled

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()
}

private const val CREATED = 201
private const val NOT_FOUND_ERROR_BODY = "errorBody를 찾을 수 없습니다."

fun <T : Any> handleApiResponse2(execute: () -> Response<T>): ResponseResult<T> {
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
                val errorResponse: ErrorResponse = StaccatoClient.getErrorResponse(errorBody)
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
