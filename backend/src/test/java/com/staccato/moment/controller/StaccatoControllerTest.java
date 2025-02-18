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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.moment.MomentDetailResponseFixture;
import com.staccato.fixture.moment.MomentLocationResponsesFixture;
import com.staccato.fixture.staccato.StaccatoSharedResponseFixture;
import com.staccato.member.domain.Member;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.StaccatoRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;
import com.staccato.moment.service.dto.response.StaccatoShareLinkResponse;

class StaccatoControllerTest extends ControllerTest {
    static Stream<Arguments> invalidStaccatoRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 0L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "카테고리 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new StaccatoRequest(null, "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목을 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("  ", "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목을 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", null, "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "장소 이름을 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("가".repeat(31), "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 제목은 공백 포함 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", "placeName", "address", null, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 위도를 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", "placeName", "address", BigDecimal.ONE, null, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 경도를 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", "placeName", null, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토의 주소를 입력해주세요."
                ),
                Arguments.of(
                        new StaccatoRequest("staccatoTitle", "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, null, 1L, List.of("https://example.com/images/namsan_tower.jpg")),
                        "스타카토 날짜를 입력해주세요."
                )
        );
    }

    @DisplayName("스타카토 생성 요청/응답을 역직렬화/직렬화하는 것을 성공한다.")
    @Test
    void createStaccatoWithValidRequest() throws Exception {
        // given
        String staccatoRequest = """
                {
                    "staccatoTitle": "staccatoTitle",
                    "placeName": "placeName",
                    "address": "address",
                    "latitude": 1.0,
                    "longitude": 1.0,
                    "visitedAt": "2023-07-01T10:00:00",
                    "categoryId": 1,
                    "staccatoImageUrls": ["https://example.com/images/namsan_tower.jpg"]
                }
                """;
        String staccatoIdResponse = """
                {
                    "staccatoId": 1
                }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staccatoRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/staccatos/1"))
                .andExpect(content().json(staccatoIdResponse));
    }

    @DisplayName("올바르지 않은 날짜 형식으로 스타카토 생성을 요청하면 예외가 발생한다.")
    @Test
    void failCreateStaccatoWithInvalidVisitedAt() throws Exception {
        // given
        String staccatoRequest = """
                    {
                        "staccatoTitle": "재밌었던 런던 박물관에서의 기억",
                        "placeName": "British Museum",
                        "address": "Great Russell St, London WC1B 3DG",
                        "latitude": 51.51978412729915,
                        "longitude": -0.12712788587027796,
                        "visitedAt": "2024/07/27T10:00:00",
                        "categoryId": 1
                    }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staccatoRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다. 올바른 형식으로 데이터를 제공해주세요."));
    }

    @DisplayName("사용자가 잘못된 요청 형식으로 정보를 입력하면, 스타카토를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidStaccatoRequestProvider")
    void failCreateStaccato(StaccatoRequest staccatoRequest, String expectedMessage) throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(momentService.createMoment(any(MomentRequest.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(post("/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staccatoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccato() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MomentLocationResponses responses = MomentLocationResponsesFixture.create();
        when(momentService.readAllMoment(any(Member.class))).thenReturn(responses);
        String expectedResponse = """
                {
                    "staccatoLocationResponses": [
                         {
                             "staccatoId": 1,
                             "latitude": 1,
                             "longitude": 0
                         },
                         {
                             "staccatoId": 2,
                             "latitude": 1,
                             "longitude": 0
                         },
                         {
                             "staccatoId": 3,
                             "latitude": 1,
                             "longitude": 0
                         }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("적합한 경로변수를 통해 스타카토 조회에 성공한다.")
    @Test
    void readStaccatoById() throws Exception {
        // given
        long staccatoId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MomentDetailResponse response = MomentDetailResponseFixture.create(staccatoId, LocalDateTime.parse("2021-11-08T11:58:20"));
        when(momentService.readMomentById(anyLong(), any(Member.class))).thenReturn(response);
        String expectedResponse = """
                    {
                         "staccatoId": 1,
                         "categoryId": 1,
                         "categoryTitle": "memoryTitle",
                         "startAt": "2024-06-30",
                         "endAt": "2024-07-04",
                         "staccatoTitle": "staccatoTitle",
                         "staccatoImageUrls": ["https://example1.com.jpg"],
                         "visitedAt": "2021-11-08T11:58:20",
                         "feeling": "happy",
                         "placeName": "placeName",
                         "address": "address",
                         "latitude": 37.7749,
                         "longitude": -122.4194
                     }
                """;

        // when & then
        mockMvc.perform(get("/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 스타카토 조회에 실패한다.")
    @Test
    void failReadStaccatoById() throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/staccatos/{staccatoId}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합한 경로변수를 통해 스타카토 수정에 성공한다.")
    @Test
    void updateStaccatoById() throws Exception {
        // given
        long staccatoId = 1L;
        String staccatoRequest = """
                {
                    "staccatoTitle": "staccatoTitle",
                    "placeName": "placeName",
                    "address": "address",
                    "latitude": 1.0,
                    "longitude": 1.0,
                    "visitedAt": "2023-07-01T10:00:00",
                    "categoryId": 1,
                    "staccatoImageUrls": [
                        "https://example.com/images/namsan_tower.jpg"
                    ]
                }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staccatoRequest))
                .andExpect(status().isOk());
    }

    @DisplayName("추가하려는 사진이 5장이 넘는다면 스타카토 수정에 실패한다.")
    @Test
    void failUpdateStaccatoByImagesSize() throws Exception {
        // given
        long staccatoId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        StaccatoRequest staccatoRequest = new StaccatoRequest("staccatoTitle", "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L,
                List.of("https://example.com/images/namsan_tower1.jpg",
                        "https://example.com/images/namsan_tower2.jpg",
                        "https://example.com/images/namsan_tower3.jpg",
                        "https://example.com/images/namsan_tower4.jpg",
                        "https://example.com/images/namsan_tower5.jpg",
                        "https://example.com/images/namsan_tower6.jpg"));
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/staccatos/{StaccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staccatoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 스타카토 수정에 실패한다.")
    @Test
    void failUpdateStaccatoById() throws Exception {
        // given
        long staccatoId = 0L;
        StaccatoRequest staccatoRequest = new StaccatoRequest("staccatoTitle", "placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L, List.of("https://example.com/images/namsan_tower.jpg"));
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staccatoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("스타카토를 삭제한다.")
    @Test
    void deleteStaccatoById() throws Exception {
        // given
        long staccatoId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(delete("/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("양수가 아닌 id로 스타카토를 삭제할 수 없다.")
    @Test
    void failDeleteStaccatoById() throws Exception {
        // given
        long staccatoId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("기분 선택을 하지 않은 경우 기분 수정에 실패한다.")
    @Test
    void failUpdateStaccatoFeelingById() throws Exception {
        // given
        long staccatoId = 1L;
        FeelingRequest feelingRequest = new FeelingRequest(null);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "기분 값을 입력해주세요.");

        // when & then
        mockMvc.perform(post("/staccatos/{staccatoId}/feeling", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .content(objectMapper.writeValueAsString(feelingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("스타카토 링크 생성에 성공한다.")
    @Test
    void createStaccatoShareLink() throws Exception {
        // given
        long staccatoId = 1L;
        StaccatoShareLinkResponse response = new StaccatoShareLinkResponse(staccatoId, "https://staccato.kr/staccato?token=sample-token");
        when(staccatoService.createStaccatoShareLink(staccatoId)).thenReturn(response);
        String expectedResponse = """
                {
                    "staccatoId": 1,
                    "shareLink": "https://staccato.kr/staccato?token=sample-token"
                }
                """;

        // when & then
        mockMvc.perform(get("/staccatos/{staccatoId}/share", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("토큰으로 스타카토의 정보를 불러온다.")
    @Test
    void readSharedStaccatoByToken() throws Exception {
        // given
        String token = "sample-token";
        when(staccatoService.readSharedStaccatoByToken(token)).thenReturn(StaccatoSharedResponseFixture.create());
        String expectedResponse = """
                {
                    "userName": "폭포",
                    "staccatoImageUrls": [
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png"
                    ],
                    "staccatoTitle": "귀여운 스타카토 키링",
                    "placeName": "한국 루터회관 8층",
                    "address": "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층",
                    "visitedAt": "2024-09-29T17:00:00.000Z",
                    "feeling": "scared",
                    "comments": [
                        {
                            "nickname": "폭포",
                            "content": "댓글 샘플",
                            "memberImageUrl": "image.jpg"
                        },
                        {
                            "nickname": "폭포2",
                            "content": "댓글 샘플2",
                            "memberImageUrl": "image2.jpg"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/staccatos/shared")
                        .param("sharedToken", token)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
