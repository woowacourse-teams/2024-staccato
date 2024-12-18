package com.on.staccato.data

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.data.comment.CommentApiService
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.memory.MemoryApiService
import com.on.staccato.errorBy400
import com.on.staccato.errorBy401
import com.on.staccato.errorBy403
import com.on.staccato.errorBy413
import com.on.staccato.errorBy500
import com.on.staccato.invalidMemoryRequest
import com.on.staccato.makeFakeImageFile
import com.on.staccato.memoryCreationResponse
import com.on.staccato.memoryResponse
import com.on.staccato.validMemoryRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class ApiResultCallAdapterTest {
    private val mockWebServer = MockWebServer()

    private lateinit var memoryApiService: MemoryApiService
    private lateinit var imageApiService: ImageApiService
    private lateinit var commentApiService: CommentApiService

    @BeforeEach
    fun setUp() {
        mockWebServer.start()

        val retrofit = buildRetrofitFor(mockWebServer)
        memoryApiService = retrofit.create(MemoryApiService::class.java)
        imageApiService = retrofit.create(ImageApiService::class.java)
        commentApiService = retrofit.create(CommentApiService::class.java)
    }

    @Test
    fun `존재하는 카테고리를 조회하면 카테고리 조회에 성공한다`() {
        val success: MockResponse =
            makeMockResponse(
                code = 200,
                body = memoryResponse,
            )
        mockWebServer.enqueue(success)

        runTest {
            val actual: ApiResult<MemoryResponse> =
                memoryApiService.getMemory(memoryId = 1)

            assertTrue(actual is Success)
        }
    }

    @Test
    fun `유효한 형식의 카테고리로 생성을 요청하면 카테고리 생성에 성공한다`() {
        val success: MockResponse =
            makeMockResponse(
                code = 201,
                body = memoryCreationResponse,
            )
        mockWebServer.enqueue(success)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(validMemoryRequest)

            assertTrue(actual is Success)
        }
    }

    @Test
    fun `유효하지 않은 형식의 카테고리로 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 400,
                body = errorBy400,
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(invalidMemoryRequest)

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `인증되지 않은 사용자가 카테고리 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 401,
                body = errorBy401,
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(validMemoryRequest)

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `댓글 삭제를 요청한 사용자와 댓글 작성자의 인증 정보가 일치하지 않으면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 403,
                body = errorBy403,
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<Unit> =
                commentApiService.deleteComment(commentId = 1)

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `20MB를 초과하는 사진을 업로드 요청하면 오류가 발생한다`() {
        val serverError =
            makeMockResponse(
                code = 413,
                body = errorBy413,
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<ImageResponse> =
                imageApiService.postImage(imageFile = makeFakeImageFile())

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `카테고리 생성 요청 중 서버 장애가 생기면 오류가 발생한다`() {
        val serverError =
            makeMockResponse(
                code = 500,
                body = errorBy500,
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(validMemoryRequest)

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `카테고리 생성 요청 중 서버의 응답이 없다면 예외가 발생한다`() {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(validMemoryRequest)

            assertTrue(actual is Exception)
        }
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
