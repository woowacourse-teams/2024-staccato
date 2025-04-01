package com.on.staccato.data

import com.on.staccato.CoroutinesTestExtension
import com.on.staccato.StaccatoApplication.Companion.retrofit
import com.on.staccato.data.dto.GetResponse
import com.on.staccato.data.dto.PostResponse
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.Exception
import com.on.staccato.data.network.ServerError
import com.on.staccato.data.network.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class ApiResultCallAdapterTest {
    private val mockWebServer: MockWebServer = MockWebServer()

    private lateinit var imageApiService: ImageApiService
    private lateinit var fakeApiService: FakeApiService

    @BeforeEach
    fun setUp() {
        mockWebServer.start()

        retrofit = buildRetrofitFor(mockWebServer)
        imageApiService = retrofit.create(ImageApiService::class.java)
        fakeApiService = retrofit.create(FakeApiService::class.java)
    }

    @Test
    fun `존재하는 데이터를 조회하면 데이터 조회에 성공한다`() {
        val success: MockResponse =
            createMockResponse(
                code = 200,
                body = createGetResponse(),
            )
        mockWebServer.enqueue(success)

        runTest {
            val actual: ApiResult<GetResponse> = fakeApiService.get(id = 1)

            assertThat(actual).isInstanceOf(Success::class.java)
        }
    }

    @Test
    fun `유효한 형식의 데이터로 생성을 요청하면 데이터 생성에 성공한다`() {
        val success: MockResponse =
            createMockResponse(
                code = 201,
                body = createPostResponse(),
            )
        mockWebServer.enqueue(success)

        runTest {
            val actual: ApiResult<PostResponse> = fakeApiService.post(request = createValidRequest())

            assertThat(actual).isInstanceOf(Success::class.java)
        }
    }

    @Test
    fun `유효하지 않은 형식의 데이터로 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            createMockResponse(
                code = 400,
                body = createErrorBy400(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<PostResponse> = fakeApiService.post(request = createInvalidRequest())

            assertThat(actual).isInstanceOf(ServerError::class.java)
        }
    }

    @Test
    fun `인증되지 않은 사용자가 데이터 생성을 요청하면 오류가 발생한다`() {
        val serverError: MockResponse =
            createMockResponse(
                code = 401,
                body = createErrorBy401(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<PostResponse> = fakeApiService.post(request = createValidRequest())

            assertThat(actual).isInstanceOf(ServerError::class.java)
        }
    }

    @Test
    fun `데이터 삭제를 요청한 사용자와 데이터 작성자의 인증 정보가 일치하지 않으면 오류가 발생한다`() {
        val serverError: MockResponse =
            createMockResponse(
                code = 403,
                body = createErrorBy403(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<Unit> = fakeApiService.delete(id = 1)

            assertThat(actual).isInstanceOf(ServerError::class.java)
        }
    }

    @Test
    fun `서버의 제한 용량을 초과하는 사진을 업로드하면 오류가 발생한다`() {
        val serverError =
            createMockResponse(
                code = 413,
                body = createErrorBy413(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<ImageResponse> = imageApiService.postImage(imageFile = createFakeImageFile())

            assertThat(actual).isInstanceOf(ServerError::class.java)
        }
    }

    @Test
    fun `데이터 생성 요청 중 서버 장애가 생기면 오류가 발생한다`() {
        val serverError =
            createMockResponse(
                code = 500,
                body = createErrorBy500(),
            )
        mockWebServer.enqueue(serverError)

        runTest {
            val actual: ApiResult<PostResponse> = fakeApiService.post(request = createValidRequest())

            assertThat(actual).isInstanceOf(ServerError::class.java)
        }
    }

    @Test
    fun `데이터 생성 요청 중 서버의 응답이 없다면 예외가 발생한다`() {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))

        runTest {
            val actual: ApiResult<PostResponse> = fakeApiService.post(request = createValidRequest())

            assertThat(actual).isInstanceOf(Exception::class.java)
        }
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
