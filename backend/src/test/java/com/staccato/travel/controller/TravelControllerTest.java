package com.staccato.travel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.member.domain.Member;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelIdResponse;

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

    static Stream<TravelRequest> travelRequestProvider() {
        return Stream.of(
                new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new TravelRequest(null, "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", null, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10))
        );
    }

    static Stream<Arguments> invalidTravelRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", null, "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "여행 제목을 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", null, LocalDate.of(2023, 7, 10)),
                        "여행 시작 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), null),
                        "여행 끝 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "가".repeat(31), "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "제목의 최대 허용 글자수는 공백 포함 30자입니다."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "가".repeat(501), LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."
                )
        );
    }

    @DisplayName("사용자가 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @ParameterizedTest
    @MethodSource("travelRequestProvider")
    void createTravel(TravelRequest travelRequest) throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("travelThumbnail", "example.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(travelService.createTravel(any(), any(), any())).thenReturn(new TravelIdResponse(1));

        // when & then
        mockMvc.perform(multipart("/travels")
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(travelRequest).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/travels/1"))
                .andExpect(jsonPath("$.travelId").value(1));
    }

    @DisplayName("사용자가 썸네일 사진 없이 새로운 여행 상세를 생성한다.")
    @Test
    void createTravel() throws Exception {
        // given
        TravelRequest travelRequest = new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(travelService.createTravel(any(), any(), any())).thenReturn(new TravelIdResponse(1));

        // when & then
        mockMvc.perform(multipart("/travels")
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(travelRequest).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/travels/1"))
                .andExpect(jsonPath("$.travelId").value(1));
    }

    @DisplayName("사용자가 잘못된 방식으로 정보를 입력하면, 여행 상세를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidTravelRequestProvider")
    void failCreateTravel(TravelRequest travelRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(travelService.createTravel(any(TravelRequest.class), any(MultipartFile.class), any(Member.class))).thenReturn(new TravelIdResponse(1));
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(multipart("/travels")
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(travelRequest).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
