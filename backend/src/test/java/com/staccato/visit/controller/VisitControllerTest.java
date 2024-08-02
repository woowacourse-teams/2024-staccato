package com.staccato.visit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.visit.service.VisitService;
import com.staccato.visit.service.dto.request.VisitRequest;

@WebMvcTest(controllers = VisitController.class)
class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VisitService visitService;
    @Autowired
    private ObjectMapper objectMapper;

    @Disabled
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
        when(visitService.createVisit(any(VisitRequest.class))).thenReturn(1L);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/visits")
                        .file(visitRequestPart)
                        .file(imageFilePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/visits/1"))
                .andExpect(jsonPath("$.visitId").value(1));
    }

    private static VisitRequest getVisitRequest(LocalDate visitedAt) {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), visitedAt, 1L);
    }
}
