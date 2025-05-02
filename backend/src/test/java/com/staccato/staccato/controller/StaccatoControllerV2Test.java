package com.staccato.staccato.controller;

import java.math.BigDecimal;
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
import com.staccato.staccato.service.dto.request.ReadStaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StaccatoControllerV2Test extends ControllerTest {

    static Stream<Arguments> invalidLatLngProvider() {
        return Stream.of(
                Arguments.of("neLat", "-91.0", "위도는 -90.0 이상이어야 합니다."),
                Arguments.of("neLat", "91.0", "위도는 90.0 이하여야 합니다."),
                Arguments.of("neLng", "-181.0", "경도는 -180.0 이상이어야 합니다."),
                Arguments.of("neLng", "181.0", "경도는 180.0 이하여야 합니다.")
        );
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccato() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().withColor(Color.PINK).build();
        StaccatoLocationResponseV2 response1 = new StaccatoLocationResponseV2(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(BigDecimal.ZERO, BigDecimal.ZERO).build(),
                category.getColor()
        );
        StaccatoLocationResponseV2 response2 = new StaccatoLocationResponseV2(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(new BigDecimal("123.456789"), new BigDecimal("123.456789")).build(),
                category.getColor()
        );
        StaccatoLocationResponsesV2 responses = new StaccatoLocationResponsesV2(List.of(response1, response2));

        when(staccatoService.readAllStaccato(any(Member.class), any(ReadStaccatoRequest.class))).thenReturn(responses);
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
        mockMvc.perform(get("/v2/staccatos")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("위경도 범위 내 스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccatoByLocation() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().withColor(Color.PINK).build();
        StaccatoLocationResponseV2 response1 = new StaccatoLocationResponseV2(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(new BigDecimal("37.5665"), new BigDecimal("125.456789")).build(),
                category.getColor()
        );
        StaccatoLocationResponseV2 response2 = new StaccatoLocationResponseV2(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(new BigDecimal("37.5665"), new BigDecimal("123.456789")).build(),
                category.getColor()
        );
        StaccatoLocationResponsesV2 responses = new StaccatoLocationResponsesV2(List.of(response1, response2));

        when(staccatoService.readAllStaccato(any(Member.class), any(ReadStaccatoRequest.class))).thenReturn(responses);
        String expectedResponse = """
                {
                    "staccatoLocationResponses": [
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 37.5665,
                             "longitude": 125.456789
                         },
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 37.5665,
                             "longitude": 123.456789
                         }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/staccatos")
                        .param("neLat", "38.0")
                        .param("neLng", "126.0")
                        .param("swLat", "37.0")
                        .param("swLng", "123.0")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("유효하지 않은 위도 또는 경도 쿼리로 스타카토 목록 조회 시 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("invalidLatLngProvider")
    void failReadAllStaccatoWithInvalidSingleLatLng(String invalidParamKey, String invalidParamValue, String expectedMessage) throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(),
                expectedMessage);

        // when & then
        mockMvc.perform(get("/v2/staccatos")
                        .param("neLat", invalidParamKey.equals("neLat") ? invalidParamValue : "37.5")
                        .param("neLng", invalidParamKey.equals("neLng") ? invalidParamValue : "127.0")
                        .param("swLat", "37.0")
                        .param("swLng", "126.8")
                        .param(invalidParamKey, invalidParamValue)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
