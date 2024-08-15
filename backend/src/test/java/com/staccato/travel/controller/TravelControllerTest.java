package com.staccato.travel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.member.domain.Member;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.service.TravelService;
import com.staccato.travel.service.dto.request.MemoryRequest;
import com.staccato.travel.service.dto.response.MemoryDetailResponse;
import com.staccato.travel.service.dto.response.MemoryIdResponse;
import com.staccato.travel.service.dto.response.MemoryResponse;
import com.staccato.travel.service.dto.response.MemoryResponses;

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

    static Stream<MemoryRequest> travelRequestProvider() {
        return Stream.of(
                new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new MemoryRequest(null, "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", null, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10))
        );
    }

    static Stream<Arguments> invalidTravelRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", null, "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "여행 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", "  ", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "여행 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", null, LocalDate.of(2023, 7, 10)),
                        "여행 시작 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), null),
                        "여행 끝 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", "가".repeat(31), "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "제목은 공백 포함 1자 이상 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "가".repeat(501), LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."
                )
        );
    }

    @DisplayName("사용자가 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @ParameterizedTest
    @MethodSource("travelRequestProvider")
    void createTravel(MemoryRequest memoryRequest) throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(travelService.createTravel(any(), any(), any())).thenReturn(new MemoryIdResponse(1));

        // when & then
        mockMvc.perform(multipart("/memories")
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(memoryRequest).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/memories/1"))
                .andExpect(jsonPath("$.memoryId").value(1));
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 여행 상세를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidTravelRequestProvider")
    void failCreateTravel(MemoryRequest memoryRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(travelService.createTravel(any(MemoryRequest.class), any(MultipartFile.class), any(Member.class))).thenReturn(new MemoryIdResponse(1));
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(multipart("/memories")
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(memoryRequest).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 모든 여행 상세 목록을 조회한다.")
    @Test
    void readAllTravel() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        MemoryResponses memoryResponses = new MemoryResponses(List.of(new MemoryResponse(createTravel())));
        when(travelService.readAllTravels(any(Member.class), any())).thenReturn(memoryResponses);

        // when & then
        mockMvc.perform(get("/memories")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(memoryResponses)));
    }

    @DisplayName("사용자가 특정 여행 상세를 조회한다.")
    @Test
    void readTravel() throws Exception {
        // given
        long travelId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        MemoryDetailResponse memoryDetailResponse = new MemoryDetailResponse(createTravel(), List.of());
        when(travelService.readTravelById(anyLong(), any(Member.class))).thenReturn(memoryDetailResponse);

        // when & then
        mockMvc.perform(get("/memories/{travelId}", travelId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(memoryDetailResponse)));
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

    @DisplayName("적합한 경로변수와 데이터를 통해 방문 기록 수정에 성공한다.")
    @ParameterizedTest
    @MethodSource("travelRequestProvider")
    void updateTravel(MemoryRequest memoryRequest) throws Exception {
        // given
        long travelId = 1L;
        MockMultipartFile file = new MockMultipartFile("memoryThumbnailUrl", "example.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/memories/{travelId}", travelId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(memoryRequest).getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 여행 상세를 수정할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidTravelRequestProvider")
    void failUpdateTravel(MemoryRequest memoryRequest, String expectedMessage) throws Exception {
        // given
        long travelId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(multipart("/memories/{travelId}", travelId)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(memoryRequest).getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 여행 상세 수정에 실패한다.")
    @Test
    void failUpdateTravelByInvalidPath() throws Exception {
        // given
        long travelId = 0L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "여행 식별자는 양수로 이루어져야 합니다.");
        MemoryRequest memoryRequest = new MemoryRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));

        // when & then
        mockMvc.perform(multipart("/memories/{travelId}", travelId)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(memoryRequest).getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 여행 식별자로 여행을 삭제한다.")
    @Test
    void deleteTravel() throws Exception {
        // given
        long travelId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(delete("/memories/{travelId}", travelId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 여행 식별자로 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteTravelByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "여행 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/memories/{travelId}", invalidId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
