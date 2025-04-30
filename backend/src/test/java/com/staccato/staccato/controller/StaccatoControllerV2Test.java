package com.staccato.staccato.controller;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StaccatoControllerV2Test extends ControllerTest {

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

        when(staccatoService.readAllStaccato(any(Member.class))).thenReturn(responses);
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
}
