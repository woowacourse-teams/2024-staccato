package com.staccato.travel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.member.domain.Member;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
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
        TravelResponses travelResponses = new TravelResponses(List.of(new TravelResponse(createTravel())));
        when(travelService.readAllTravels(any(Member.class), any())).thenReturn(travelResponses);

        // when & then
        mockMvc.perform(get("/travels")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(travelResponses)));
    }

    @DisplayName("사용자가 특정 여행 상세를 조회한다.")
    @Test
    void readTravel() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        TravelDetailResponse travelDetailResponse = new TravelDetailResponse(createTravel(), List.of());
        when(travelService.readTravelById(anyLong(), any(Member.class))).thenReturn(travelDetailResponse);

        // when & then
        mockMvc.perform(get("/travels/1")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(travelDetailResponse)));
    }

    private Travel createTravel() {
        return Travel.builder()
                .thumbnailUrl("https://example.com/travels/geumohrm.jpg")
                .title("2024 여름 휴가")
                .description("친구들과 함께한 여름 휴가 여행")
                .startAt(LocalDate.of(2024, 7, 1))
                .endAt(LocalDate.of(2024, 7, 10))
                .build();
    }

    @DisplayName("사용자가 잘못된 여행 식별자로 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadTravelByInvalidId() throws Exception {
        // given
        long invalidId = -1;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "여행 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/travels/" + invalidId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
