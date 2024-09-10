package com.staccato.moment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.moment.MomentDetailResponseFixture;
import com.staccato.fixture.moment.MomentLocationResponsesFixture;
import com.staccato.member.domain.Member;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;

@WebMvcTest(controllers = MomentController.class)
class MomentControllerTest {
    private static final String VALID_UPDATE_REQUEST_JSON = "{"
            + "\"placeName\": \"placeName\","
            + "\"address\": \"address\","
            + "\"latitude\": 1.0,"
            + "\"longitude\": 1.0,"
            + "\"visitedAt\": \"2023-07-01T10:00:00\","
            + "\"memoryId\": 1,"
            + "\"momentImageUrls\": ["
            + "  \"https://example.com/images/namsan_tower.jpg\""
            + "]"
            + "}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MomentService momentService;
    @MockBean
    private AuthService authService;

    static Stream<Arguments> invalidMomentRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 0L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "추억 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new MomentRequest(null, "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("  ", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("가".repeat(31), "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목은 공백 포함 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", "address", null, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 위도를 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", "address", BigDecimal.ONE, null, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 경도를 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", null, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 주소를 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, null, 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 날짜를 입력해주세요."
                )
        );
    }

    @DisplayName("스타카토 생성 시 사진 5장까지는 첨부 가능하다.")
    @Test
    void createMoment() throws Exception {
        // given
        MomentRequest momentRequest = new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L,
                List.of("https://example.com/images/namsan_tower1.jpg",
                        "https://example.com/images/namsan_tower2.jpg",
                        "https://example.com/images/namsan_tower3.jpg",
                        "https://example.com/images/namsan_tower4.jpg",
                        "https://example.com/images/namsan_tower5.jpg"));
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        MomentIdResponse momentIdResponse = new MomentIdResponse(1L);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/moments")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(momentRequestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/moments/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(momentIdResponse)));
    }

    @DisplayName("올바르지 않은 날짜 형식으로 스타카토 생성을 요청하면 예외가 발생한다.")
    @Test
    void failCreateMomentWithInvalidVisitedAt() throws Exception {
        // given
        String momentRequestJson = """
                    {
                        "placeName": "런던 박물관",
                        "address": "Great Russell St, London WC1B 3DG",
                        "latitude": 51.51978412729915,
                        "longitude": -0.12712788587027796,
                        "visitedAt": "2024/07/27T10:00:00",
                        "memoryId": 1
                    }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/moments")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(momentRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다. 올바른 형식으로 데이터를 제공해주세요."));
    }

    @DisplayName("사진이 5장을 초과하면 스타카토 생성에 실패한다.")
    @Test
    void failCreateMomentByImageCount() throws Exception {
        // given
        MomentRequest momentRequest = new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L,
                List.of("https://example.com/images/namsan_tower1.jpg",
                        "https://example.com/images/namsan_tower2.jpg",
                        "https://example.com/images/namsan_tower3.jpg",
                        "https://example.com/images/namsan_tower4.jpg",
                        "https://example.com/images/namsan_tower5.jpg",
                        "https://example.com/images/namsan_tower6.jpg"));
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(post("/moments")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(momentRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 잘못된 요청 형식으로 정보를 입력하면, 스타카토를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidMomentRequestProvider")
    void failCreateMoment(MomentRequest momentRequest, String expectedMessage) throws Exception {
        // given
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/moments")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(momentRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllMoment() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MomentLocationResponses responses = MomentLocationResponsesFixture.create();
        when(momentService.readAllMoment(any(Member.class))).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/moments")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responses)));
    }

    @DisplayName("적합한 경로변수를 통해 스타카토 조회에 성공한다.")
    @Test
    void readMomentById() throws Exception {
        // given
        long momentId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MomentDetailResponse response = MomentDetailResponseFixture.create(momentId, LocalDateTime.now());
        when(momentService.readMomentById(anyLong(), any(Member.class))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 스타카토 조회에 실패한다.")
    @Test
    void failReadMomentById() throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/moments/{momentId}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합한 경로변수를 통해 스타카토 수정에 성공한다.")
    @Test
    void updateMomentById() throws Exception {
        // given
        long momentId = 1L;
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk());
    }

    @DisplayName("추가하려는 사진이 5장이 넘는다면 스타카토 수정에 실패한다.")
    @Test
    void failUpdateMomentByImagesSize() throws Exception {
        // given
        long momentId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName",
                List.of("https://example.com/images/namsan_tower1.jpg",
                        "https://example.com/images/namsan_tower2.jpg",
                        "https://example.com/images/namsan_tower3.jpg",
                        "https://example.com/images/namsan_tower4.jpg",
                        "https://example.com/images/namsan_tower5.jpg",
                        "https://example.com/images/namsan_tower6.jpg"));
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 스타카토 수정에 실패한다.")
    @Test
    void failUpdateMomentById() throws Exception {
        // given
        long momentId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("스타카토 수정 시 장소 이름을 입력하지 않은 경우 수정에 실패한다.")
    @Test
    void failUpdateMomentByPlaceName() throws Exception {
        // given
        long momentId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 제목을 입력해주세요.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest(null, List.of("https://example1.com.jpg"));
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Nested
    @DisplayName("updateMomentByIdV2 테스트")
    class updateMomentByIdV2Test {
        @DisplayName("적합한 경로변수를 통해 스타카토 수정에 성공한다.")
        @Test
        void updateMomentById() throws Exception {
            // given
            long momentId = 1L;
            when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

            // when & then
            mockMvc.perform(put("/moments/v2/{momentId}", momentId)
                            .header(HttpHeaders.AUTHORIZATION, "token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_REQUEST_JSON))
                    .andExpect(status().isOk());
        }

        @DisplayName("추가하려는 사진이 5장이 넘는다면 스타카토 수정에 실패한다.")
        @Test
        void failUpdateMomentByImagesSize() throws Exception {
            // given
            long momentId = 1L;
            ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
            String updateRequestWithTooManyImagesJson = "{"
                    + "\"placeName\": \"placeName\","
                    + "\"address\": \"address\","
                    + "\"latitude\": 1.0,"
                    + "\"longitude\": 1.0,"
                    + "\"visitedAt\": \"2023-07-01T10:00:00\","
                    + "\"memoryId\": 1,"
                    + "\"momentImageUrls\": ["
                    + "  \"https://example.com/images/namsan_tower1.jpg\","
                    + "  \"https://example.com/images/namsan_tower2.jpg\","
                    + "  \"https://example.com/images/namsan_tower3.jpg\","
                    + "  \"https://example.com/images/namsan_tower4.jpg\","
                    + "  \"https://example.com/images/namsan_tower5.jpg\","
                    + "  \"https://example.com/images/namsan_tower6.jpg\""
                    + "]"
                    + "}";
            when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

            // when & then
            mockMvc.perform(put("/moments/v2/{momentId}", momentId)
                            .header(HttpHeaders.AUTHORIZATION, "token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestWithTooManyImagesJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
        }

        @DisplayName("적합하지 않은 경로변수의 경우 스타카토 수정에 실패한다.")
        @Test
        void failUpdateMomentById() throws Exception {
            // given
            long momentId = 0L;
            ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");
            when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

            // when & then
            mockMvc.perform(put("/moments/v2/{momentId}", momentId)
                            .header(HttpHeaders.AUTHORIZATION, "token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_REQUEST_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
        }

        /*
        TODO: 컨트롤러 테스트 코드 리팩토링 PR이 머지되면
         해당 코드 참고하여 MomentRequest의 필수값 필드가 빠져있는 경우에 대한 테스트 추가
         */
    }

    @DisplayName("스타카토를 삭제한다.")
    @Test
    void deleteMomentById() throws Exception {
        // given
        long momentId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(delete("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("양수가 아닌 id로 스타카토를 삭제할 수 없다.")
    @Test
    void failDeleteMomentById() throws Exception {
        // given
        long momentId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("기분 선택을 하지 않은 경우 기분 수정에 실패한다.")
    @Test
    void failUpdateMomentFeelingById() throws Exception {
        // given
        long momentId = 1L;
        FeelingRequest feelingRequest = new FeelingRequest(null);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "기분 값을 입력해주세요.");

        // when & then
        mockMvc.perform(post("/moments/{momentId}/feeling", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .content(objectMapper.writeValueAsString(feelingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
