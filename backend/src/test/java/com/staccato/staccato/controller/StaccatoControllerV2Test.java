package com.staccato.staccato.controller;

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
import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StaccatoControllerV2Test extends ControllerTest {

    static Stream<Arguments> invalidLatLngProvider() {
        return Stream.of(
                Arguments.of("37.5", "127.0", "-90.1", "126.8", "위도는 -90.0 이상이어야 합니다."),
                Arguments.of("37.5", "127.0", "90.1", "126.8", "위도는 90.0 이하여야 합니다."),
                Arguments.of("37.5", "127.0", "37.0", "-180.1", "경도는 -180.0 이상이어야 합니다."),
                Arguments.of("37.5", "127.0", "37.0", "180.1", "경도는 180.0 이하여야 합니다.")
        );
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccato() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().withColor(Color.PINK).build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withSpot(BigDecimal.ZERO, BigDecimal.ZERO)
                .withTitle("title")
                .withVisitedAt(LocalDateTime.of(2024, 5, 1, 0, 0))
                .build();
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withSpot(new BigDecimal("123.456789"), new BigDecimal("123.456789"))
                .withTitle("title2")
                .withVisitedAt(LocalDateTime.of(2024, 5, 2, 0, 0))
                .build();
        StaccatoLocationResponsesV2 responses = StaccatoLocationResponsesV2.of(List.of(staccato, staccato2));

        when(staccatoService.readAllStaccato(any(Member.class), any(StaccatoLocationRangeRequest.class))).thenReturn(responses);
        String expectedResponse = """
                {
                    "staccatoLocationResponses": [
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 0,
                             "longitude": 0,
                             "title": "title",
                             "visitedAt": "2024-05-01T00:00:00"
                         },
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 123.456789,
                             "longitude": 123.456789,
                             "title": "title2",
                             "visitedAt": "2024-05-02T00:00:00"
                         }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse, true));
    }

    @DisplayName("유효하지 않은 위도 또는 경도 쿼리로 스타카토 목록 조회 시 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("invalidLatLngProvider")
    void failReadAllStaccatoWithInvalidSingleLatLng(String neLat, String neLng, String swLat, String swLng, String expectedMessage) throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(),
                expectedMessage);

        // when & then
        mockMvc.perform(get("/v2/staccatos")
                        .param("neLat", neLat)
                        .param("neLng", neLng)
                        .param("swLat", swLat)
                        .param("swLng", swLng)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
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
        StaccatoDetailResponseV2 response = new StaccatoDetailResponseV2(staccato);
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
                         "longitude": 0,
                         "isShared": false
                     }
                """;

        // when & then
        mockMvc.perform(get("/v2/staccatos/{staccatoId}", staccatoId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse, true));
    }
}
