package com.staccato.staccato.controller;

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
import java.time.LocalDate;
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
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.comment.domain.Comment;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.fixture.staccato.StaccatoRequestFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoIdResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;

class StaccatoControllerTest extends ControllerTest {

    static Stream<Arguments> invalidStaccatoRequestProvider() {
        return Stream.of(
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withCategoryId(0L).build(),
                        "카테고리 식별자는 양수로 이루어져야 합니다."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withStaccatoTitle(null).build(),
                        "스타카토 제목을 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withStaccatoTitle("").build(),
                        "스타카토 제목을 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withPlaceName(null).build(),
                        "장소 이름을 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withStaccatoTitle("가".repeat(31)).build(),
                        "스타카토 제목은 공백 포함 30자 이하로 설정해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withLatitude(null).build(),
                        "스타카토의 위도를 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withLongitude(null).build(),
                        "스타카토의 경도를 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withAddress(null).build(),
                        "스타카토의 주소를 입력해주세요."),
                Arguments.of(StaccatoRequestFixtures.defaultStaccatoRequest()
                                .withVisitedAt(null).build(),
                        "스타카토 날짜를 입력해주세요.")
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(staccatoService.createStaccato(any(StaccatoRequest.class),
                any(Member.class))).thenReturn(new StaccatoIdResponse(1L));

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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(staccatoService.createStaccato(any(StaccatoRequest.class),
                any(Member.class))).thenReturn(new StaccatoIdResponse(1L));

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
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), expectedMessage);
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(staccatoService.createStaccato(any(StaccatoRequest.class),
                any(Member.class))).thenReturn(new StaccatoIdResponse(1L));

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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().withColor(Color.PINK).build();
        StaccatoLocationResponse response1 = new StaccatoLocationResponse(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(BigDecimal.ZERO, BigDecimal.ZERO).build(),
                category.getColor()
        );
        StaccatoLocationResponse response2 = new StaccatoLocationResponse(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(new BigDecimal("123.456789"), new BigDecimal("123.456789")).build(),
                category.getColor()
        );
        StaccatoLocationResponses responses = new StaccatoLocationResponses(List.of(response1, response2));

        when(staccatoService.readAllStaccato(any(Member.class))).thenReturn(responses);
        String expectedResponse = """
                {
                    "staccatoLocationResponses": [
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 0,
                             "longitude": 0
                         },
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 123.456789,
                             "longitude": 123.456789
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        StaccatoDetailResponse response = new StaccatoDetailResponse(staccato);
        when(staccatoService.readStaccatoById(anyLong(), any(Member.class))).thenReturn(response);
        String expectedResponse = """
                    {
                         "staccatoId": null,
                         "categoryId": null,
                         "categoryTitle": "categoryTitle",
                         "startAt": "2024-01-01",
                         "endAt": "2024-12-31",
                         "staccatoTitle": "staccatoTitle",
                         "staccatoImageUrls": ["https://example.com/staccatoImage.jpg"],
                         "visitedAt": "2024-06-01T00:00:00",
                         "feeling": "nothing",
                         "placeName": "placeName",
                         "address": "address",
                         "latitude": 0,
                         "longitude": 0
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
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

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
                    "visitedAt": "2023-06-01T00:00:00",
                    "categoryId": 1,
                    "staccatoImageUrls": [
                        "https://example.com/staccatoImage.jpg"
                    ]
                }
                """;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

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
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
                .withStaccatoImageUrls(List.of(
                        "https://example.com/staccatoImage1.jpg", "https://example.com/staccatoImage2.jpg",
                        "https://example.com/staccatoImage3.jpg", "https://example.com/staccatoImage4.jpg",
                        "https://example.com/staccatoImage5.jpg", "https://example.com/staccatoImage6.jpg")).build();
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(put("/staccatos/{staccatoId}", staccatoId)
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
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest().build();
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

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
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

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
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "기분 값을 입력해주세요.");

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
        StaccatoShareLinkResponse response = new StaccatoShareLinkResponse(staccatoId, "https://staccato.kr/share/sample-token", "sample-token");
        when(staccatoShareService.createStaccatoShareLink(any(), any())).thenReturn(response);
        String expectedResponse = """
                {
                    "staccatoId": 1,
                    "shareLink": "https://staccato.kr/share/sample-token"
                }
                """;

        // when & then
        mockMvc.perform(post("/staccatos/{staccatoId}/share", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/staccatos/shared/" + "sample-token"))
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("토큰으로 스타카토의 정보를 불러온다.")
    @Test
    void readSharedStaccatoByToken() throws Exception {
        // given
        String token = "sample-token";
        LocalDateTime expiredAt = LocalDateTime.of(2024, 6, 1, 0, 0, 0);
        Category category = CategoryFixtures.defaultCategory().build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/stacccatoImage1.jpg",
                        "https://example.com/stacccatoImage2.jpg")).build();
        Member member1 = MemberFixtures.defaultMember()
                .withNickname("nickname1")
                .withImageUrl("memberImageUrl1.jpg").build();
        Member member2 = MemberFixtures.defaultMember()
                .withNickname("nickname2")
                .withImageUrl("memberImageUrl2.jpg").build();
        Comment comment1 = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member1).build();
        Comment comment2 = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member2).build();
        StaccatoSharedResponse staccatoSharedResponse = new StaccatoSharedResponse(expiredAt, staccato, member1, List.of(comment1, comment2));
        when(staccatoShareService.readSharedStaccatoByToken(token)).thenReturn(staccatoSharedResponse);
        String expectedResponse = """
                {
                    "staccatoId": null,
                    "expiredAt": "2024-06-01T00:00:00",
                    "nickname": "nickname1",
                    "staccatoImageUrls": [
                        "https://example.com/stacccatoImage1.jpg",
                        "https://example.com/stacccatoImage2.jpg"
                    ],
                    "staccatoTitle": "staccatoTitle",
                    "placeName": "placeName",
                    "address": "address",
                    "visitedAt": "2024-06-01T00:00:00",
                    "feeling": "nothing",
                    "comments": [
                        {
                            "nickname": "nickname1",
                            "content": "commentContent",
                            "memberImageUrl": "memberImageUrl1.jpg"
                        },
                        {
                            "nickname": "nickname2",
                            "content": "commentContent",
                            "memberImageUrl": "memberImageUrl2.jpg"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/staccatos/shared/{token}", token)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
