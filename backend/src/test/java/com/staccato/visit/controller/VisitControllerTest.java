package com.staccato.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitIdResponse;

@WebMvcTest(controllers = VisitController.class)
class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VisitService visitService;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> invalidVisitRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 0L),
                        "여행 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new VisitRequest(null, "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 1L),
                        "방문한 장소의 이름을 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", "address", null, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 1L),
                        "방문한 장소의 위도를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", "address", BigDecimal.ONE, null, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 1L),
                        "방문한 장소의 경도를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", null, BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 1L),
                        "방문한 장소의 주소를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), null),
                        "여행 상세를 선택해주세요."
                ),
                Arguments.of(
                        getVisitRequest(null),
                        "방문 날짜를 입력해주세요."
                )
        );
    }

    @DisplayName("방문 기록을 생성한다.")
    @Test
    void createVisit() throws Exception {
        // given
        VisitRequest visitRequest = getVisitRequest(LocalDate.now());
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile(
                "visitRequest",
                "visitRequest.json",
                "application/json",
                visitRequestJson.getBytes()
        );
        MockMultipartFile imageFilePart = new MockMultipartFile(
                "visitImagesFile",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );
        when(visitService.createVisit(any(VisitRequest.class))).thenReturn(new VisitIdResponse(1L));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/visits")
                        .file(visitRequestPart)
                        .file(imageFilePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/visits/1"))
                .andExpect(jsonPath("$.visitId").value(1));
    }

    @DisplayName("사진이 5장을 초과하면 방문 기록 생성에 실패한다.")
    @Test
    void failCreateVisitByImageCount() throws Exception {
        // given
        VisitRequest visitRequest = getVisitRequest(LocalDate.now());
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile(
                "visitRequest",
                "visitRequest.json",
                "application/json",
                visitRequestJson.getBytes()
        );

        List<MockMultipartFile> imageFiles = List.of(
                new MockMultipartFile("visitImagesFile", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes()),
                new MockMultipartFile("visitImagesFile", "test-image2.jpg", "image/jpeg", "dummy image content".getBytes()),
                new MockMultipartFile("visitImagesFile", "test-image3.jpg", "image/jpeg", "dummy image content".getBytes()),
                new MockMultipartFile("visitImagesFile", "test-image4.jpg", "image/jpeg", "dummy image content".getBytes()),
                new MockMultipartFile("visitImagesFile", "test-image5.jpg", "image/jpeg", "dummy image content".getBytes()),
                new MockMultipartFile("visitImagesFile", "test-image6.jpg", "image/jpeg", "dummy image content".getBytes())
        );
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/visits");
        builder.file(visitRequestPart);
        for (MockMultipartFile imageFile : imageFiles) {
            builder.file(imageFile);
        }

        // when & then
        mockMvc.perform(builder.contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사진은 5장까지만 추가할 수 있어요."));
    }

    private static VisitRequest getVisitRequest(LocalDate visitedAt) {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), visitedAt, 1L);
    }

    @DisplayName("사용자가 잘못된 방식으로 정보를 입력하면, 방문 기록을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidVisitRequestProvider")
    void failCreateVisit(VisitRequest visitRequest, String expectedMessage) throws Exception {
        // given
        String visitRequestJson = objectMapper.writeValueAsString(visitRequest);
        MockMultipartFile visitRequestPart = new MockMultipartFile(
                "visitRequest",
                "visitRequest.json",
                "application/json",
                visitRequestJson.getBytes()
        );
        MockMultipartFile imageFilePart = new MockMultipartFile(
                "visitImagesFile",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );
        when(visitService.createVisit(any(VisitRequest.class))).thenReturn(new VisitIdResponse(1L));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/visits")
                        .file(visitRequestPart)
                        .file(imageFilePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @DisplayName("방문 기록을 삭제한다.")
    @Test
    void deleteVisitById() throws Exception {
        // given
        long visitId = 1L;
        doNothing().when(visitService).deleteVisitById(anyLong());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/visits/{id}", visitId))
                .andExpect(status().isOk());
    }

    @DisplayName("양수가 아닌 id로 방문 기록을 삭제할 수 없다.")
    @Test
    void failDeleteVisitById() throws Exception {
        // given
        long visitId = 0L;
        doNothing().when(visitService).deleteVisitById(anyLong());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/visits/{id}", visitId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("방문 기록 식별자는 양수로 이루어져야 합니다."));
    }
}
