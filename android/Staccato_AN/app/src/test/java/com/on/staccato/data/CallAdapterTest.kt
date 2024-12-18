package com.on.staccato.data

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.data.comment.CommentApiService
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryRequest
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.memory.MemoryApiService
import com.on.staccato.makeFakeImageFile
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
class CallAdapterTest {
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
    fun `유효한 형식의 카테고리로 생성을 요청하면 카테고리 생성에 성공한다`() {
        val success: MockResponse =
            makeMockResponse(
                code = 201,
                body =
                    """
                    {
                        "memoryId": 1
                    }
                    """.trimIndent(),
            )
        mockWebServer.enqueue(success)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(MemoryRequest(memoryTitle = "해나의 추억"))

            assertTrue(actual is Success)
        }
    }

    @Test
    fun `유효하지 않은 형식의 카테고리로 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 400,
                body =
                    """
                    {
                        "status": "400 BAD_REQUEST"
                        "message": "추억 제목을 입력해주세요"
                    }
                    """.trimIndent(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(MemoryRequest(memoryTitle = ""))

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `인증되지 않은 사용자가 카테고리 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 401,
                body =
                    """
                    {
                        "status": "401 UNAUTHORIZED"
                        "message": "인증되지 않은 사용자입니다."
                    }
                    """.trimIndent(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(MemoryRequest(memoryTitle = "해나의 추억"))

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `댓글 삭제를 요청한 사용자와 댓글 작성자의 인증 정보가 일치하지 않으면 오류가 발생한다`() {
        val serverError: MockResponse =
            makeMockResponse(
                code = 403,
                body =
                    """
                    {
                        "status": "403 FORBIDDEN"
                        "message": "요청하신 작업을 처리할 권한이 없습니다."
                    }
                    """.trimIndent(),
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
                body =
                    """
                    {
                        "status": "413 Payload Too Large",
                        "message": "20MB 이하의 사진을 업로드해 주세요."
                    }
                    """.trimIndent(),
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
                body =
                    """
                    {
                        "status": "500 Internal Server Error",
                        "message": "예기치 못한 서버 오류입니다. 다시 시도하세요."
                    }
                    """.trimIndent(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(MemoryRequest(memoryTitle = "해나의 추억"))

            assertTrue(actual is ServerError)
        }
    }

    @Test
    fun `카테고리 생성 요청 중 서버의 응답이 없다면 예외가 발생한다`() {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))

        runTest {
            val actual: ApiResult<MemoryCreationResponse> =
                memoryApiService.postMemory(MemoryRequest(memoryTitle = "해나의 추억"))

            assertTrue(actual is Exception)
        }
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
