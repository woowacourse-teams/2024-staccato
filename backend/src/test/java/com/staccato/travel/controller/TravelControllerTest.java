package com.staccato.travel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.member.domain.Member;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.response.TravelResponse;
import com.staccato.travel.service.dto.response.TravelResponses;


@WebMvcTest(TravelController.class)
class TravelControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TravelService travelService;
    @MockBean
    private AuthService authService;

    @DisplayName("사용자가 모든 여행 상세 목록을 조회한다.")
    @Test
    void readAllTravel() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        TravelResponses travelResponses = new TravelResponses(List.of(new TravelResponse(createTravel(2024))));
        when(travelService.readAllTravels(any(Member.class), any())).thenReturn(travelResponses);

        // when & then
        mockMvc.perform(get("/travels")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(travelResponses)));
    }

    private Travel createTravel(int year) {
        return Travel.builder()
                .thumbnailUrl("https://example.com/travels/geumohrm.jpg")
                .title(year + "여름 휴가")
                .description("친구들과 함께한 여름 휴가 여행")
                .startAt(LocalDate.of(year, 7, 1))
                .endAt(LocalDate.of(year, 7, 10))
                .build();
    }
}
