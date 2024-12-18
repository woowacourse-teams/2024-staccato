package com.on.staccato.data

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryRequest
import com.on.staccato.data.memory.MemoryApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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

    @BeforeEach
    fun setUp() {
        mockWebServer.start()

        val retrofit = buildRetrofitFor(mockWebServer)
        memoryApiService = retrofit.create(MemoryApiService::class.java)
    }

    @Test
    fun `유효한 형식의 카테고리로 생성을 요청하면 카테고리 생성에 성공한다`() {
        val success: MockResponse =
            makeMockResponse(
                code = 200,
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

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
