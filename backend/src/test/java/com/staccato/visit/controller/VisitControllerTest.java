package com.staccato.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.member.domain.Member;
import com.staccato.visit.fixture.VisitDetailResponseFixture;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitIdResponse;

@WebMvcTest(controllers = VisitController.class)
class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private VisitService visitService;
    @MockBean
    private AuthService authService;

    static Stream<Arguments> invalidVisitRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 0L),
                        "여행 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new VisitRequest(null, "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "방문한 장소의 이름을 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", "address", null, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "방문한 장소의 위도를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", "address", BigDecimal.ONE, null, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "방문한 장소의 경도를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", null, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "방문한 장소의 주소를 입력해주세요."
                ),
                Arguments.of(
                        getVisitRequest(null),
                        "방문 날짜를 입력해주세요."
                )
        );
    }

    @DisplayName("방문 기록 생성 시 사진 5장까지는 첨부 가능하다.")
    @Test
    void createVisit() throws Exception {
        // given
        VisitRequest visitRequest = getVisitRequest(LocalDateTime.now());
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile("data", "visitRequest.json", "application/json", visitRequestJson.getBytes());
        MockMultipartFile file1 = new MockMultipartFile("visitImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("visitImageFiles", "test-image2.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("visitImageFiles", "test-image3.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("visitImageFiles", "test-image4.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("visitImageFiles", "test-image5.jpg", "image/jpeg", "dummy image content".getBytes());
        VisitIdResponse visitIdResponse = new VisitIdResponse(1L);
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(visitService.createVisit(any(VisitRequest.class), any(List.class), any(Member.class))).thenReturn(new VisitIdResponse(1L));

        // when & then
        mockMvc.perform(multipart("/visits")
                        .file(visitRequestPart)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/visits/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(visitIdResponse)));
    }

    @DisplayName("사진이 5장을 초과하면 방문 기록 생성에 실패한다.")
    @Test
    void failCreateVisitByImageCount() throws Exception {
        // given
        VisitRequest visitRequest = getVisitRequest(LocalDateTime.now());
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile("data", "visitRequest.json", "application/json", visitRequestJson.getBytes());
        MockMultipartFile file1 = new MockMultipartFile("visitImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("visitImageFiles", "test-image2.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("visitImageFiles", "test-image3.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("visitImageFiles", "test-image4.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("visitImageFiles", "test-image5.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("visitImageFiles", "test-image6.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartHttpServletRequestBuilder builder = multipart("/visits");
        builder.file(visitRequestPart);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/visits")
                        .file(visitRequestPart)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .file(file6)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    private static VisitRequest getVisitRequest(LocalDateTime visitedAt) {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, visitedAt, 1L);
    }

    @DisplayName("사용자가 잘못된 요청 형식으로 정보를 입력하면, 방문 기록을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidVisitRequestProvider")
    void failCreateVisit(VisitRequest visitRequest, String expectedMessage) throws Exception {
        // given
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile("data", "visitRequest.json", "application/json", visitRequestJson.getBytes());
        MockMultipartFile imageFilePart = new MockMultipartFile("visitImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(visitService.createVisit(any(VisitRequest.class), any(List.class), any(Member.class))).thenReturn(new VisitIdResponse(1L));

        // when & then
        mockMvc.perform(multipart("/visits")
                        .file(visitRequestPart)
                        .file(imageFilePart)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합한 경로변수를 통해 방문 기록 조회에 성공한다.")
    @Test
    void readVisitById() throws Exception {
        // given
        long visitId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        VisitDetailResponse response = VisitDetailResponseFixture.create(visitId, LocalDate.now());
        when(visitService.readVisitById(anyLong(), any(Member.class))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/visits/{visitId}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
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
        MockMultipartFile file = new MockMultipartFile("visitImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
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
        MockMultipartFile file1 = new MockMultipartFile("visitImageFiles", "namsan_tower1.jpg".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("visitImageFiles", "namsan_tower2.jpg".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("visitImageFiles", "namsan_tower3.jpg".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("visitImageFiles", "namsan_tower4.jpg".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("visitImageFiles", "namsan_tower5.jpg".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("visitImageFiles", "namsan_tower6.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

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
                        .header(HttpHeaders.AUTHORIZATION, "token")
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
        MockMultipartFile file = new MockMultipartFile("visitImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
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
        MockMultipartFile file = new MockMultipartFile("visitImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/visits/{visitId}", visitId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("방문 기록을 삭제한다.")
    @Test
    void deleteVisitById() throws Exception {
        // given
        long visitId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(delete("/visits/{id}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("양수가 아닌 id로 방문 기록을 삭제할 수 없다.")
    @Test
    void failDeleteVisitById() throws Exception {
        // given
        long visitId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "방문 기록 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/visits/{id}", visitId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
