package com.staccato.visit.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.exception.ExceptionResponse;
import com.staccato.visit.fixture.VisitDetailResponseFixture;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

@WebMvcTest(VisitController.class)
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private VisitService visitService;

    @DisplayName("적합한 경로변수를 통해 방문 기록 조회에 성공한다.")
    @Test
    void readVisitById() throws Exception {
        // given
        long visitId = 1L;
        VisitDetailResponse response = VisitDetailResponseFixture.create(visitId, LocalDate.now());

        // when
        when(visitService.readVisitById(visitId)).thenReturn(response);

        // then
        mockMvc.perform(get("/visits/{visitId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 방문 기록 조회에 실패한다.")
    @Test
    void failReadVisitById() throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "방문 기록 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/visits/{visitId}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
