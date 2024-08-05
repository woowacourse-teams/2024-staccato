package com.staccato.visit.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.exception.ExceptionResponse;
import com.staccato.visit.fixture.VisitDetailResponseFixture;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
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

    @DisplayName("적합한 경로변수를 통해 방문 기록 수정에 성공한다.")
    @Test
    void updateVisitById() throws Exception {
        // given
        long visitId = 1L;
        VisitUpdateRequest updateRequest = new VisitUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("visitImagesFile", "namsan_tower.jpg".getBytes());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @DisplayName("추가하려는 사진이 5장이 넘는다면 방문 기록 수정에 실패한다.")
    @Test
    void failUpdateVisitByImagesSize() throws Exception {
        // given
        long visitId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        VisitUpdateRequest updateRequest = new VisitUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file1 = new MockMultipartFile("visitImagesFile", "namsan_tower1.jpg".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("visitImagesFile", "namsan_tower2.jpg".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("visitImagesFile", "namsan_tower3.jpg".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("visitImagesFile", "namsan_tower4.jpg".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("visitImagesFile", "namsan_tower5.jpg".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("visitImagesFile", "namsan_tower6.jpg".getBytes());


        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .file(file6)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 방문 기록 수정에 실패한다.")
    @Test
    void failUpdateVisitById() throws Exception {
        // given
        long visitId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "방문 기록 식별자는 양수로 이루어져야 합니다.");
        VisitUpdateRequest updateRequest = new VisitUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("visitImagesFile", "namsan_tower.jpg".getBytes());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("방문 기록 수정 시 장소 이름을 입력하지 않은 경우 수정에 실패한다.")
    @Test
    void failUpdateVisitByPlaceName() throws Exception {
        // given
        long visitId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "방문한 장소의 이름을 입력해주세요.");
        VisitUpdateRequest updateRequest = new VisitUpdateRequest(null, List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("visitImagesFile", "namsan_tower.jpg".getBytes());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
