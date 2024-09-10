package com.staccato.memory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.memory.MemoryNameResponsesFixture;
import com.staccato.fixture.memory.MemoryRequestFixture;
import com.staccato.fixture.memory.MemoryResponsesFixture;
import com.staccato.member.domain.Member;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;

@WebMvcTest(MemoryController.class)
class MemoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemoryService memoryService;
    @MockBean
    private AuthService authService;

    static Stream<MemoryRequest> memoryRequestProvider() {
        return Stream.of(
                new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 추억", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new MemoryRequest(null, "2023 여름 휴가", "친구들과 함께한 여름 휴가 추억", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", null, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", null, null, null)
        );
    }

    static Stream<Arguments> invalidMemoryRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new MemoryRequest("https://example.com/memorys/geumohrm.jpg", null, "친구들과 함께한 여름 휴가 추억", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "추억 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "  ", "친구들과 함께한 여름 휴가 추억", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "추억 제목을 입력해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "가".repeat(31), "친구들과 함께한 여름 휴가 추억", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "제목은 공백 포함 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new MemoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", "가".repeat(501), LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."
                )
        );
    }

    @DisplayName("사용자가 추억 정보를 입력하면, 새로운 추억을 생성한다.")
    @ParameterizedTest
    @MethodSource("memoryRequestProvider")
    void createMemory(MemoryRequest memoryRequest) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(memoryService.createMemory(any(), any())).thenReturn(new MemoryIdResponse(1));

        // when & then
        mockMvc.perform(post("/memories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memoryRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/memories/1"))
                .andExpect(jsonPath("$.memoryId").value(1));
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 추억을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidMemoryRequestProvider")
    void failCreateMemory(MemoryRequest memoryRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(memoryService.createMemory(any(MemoryRequest.class), any(Member.class))).thenReturn(new MemoryIdResponse(1));
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/memories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memoryRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 모든 추억 목록을 조회한다.")
    @Test
    void readAllMemory() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MemoryResponses memoryResponses = MemoryResponsesFixture.create(MemoryFixture.create());
        when(memoryService.readAllMemories(any(Member.class))).thenReturn(memoryResponses);

        // when & then
        mockMvc.perform(get("/memories")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(memoryResponses)));
    }

    @DisplayName("특정 날짜를 포함하고 있는 모든 추억 목록을 조회한다.")
    @Test
    void readAllMemoryIncludingDate() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MemoryNameResponses memoryNameResponses = MemoryNameResponsesFixture.create(MemoryFixture.create());
        when(memoryService.readAllMemoriesIncludingDate(any(Member.class), any())).thenReturn(memoryNameResponses);
        String currentDate = "2024-07-01";

        // when & then
        mockMvc.perform(get("/memories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("currentDate", currentDate))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(memoryNameResponses)));
    }

    @DisplayName("잘못된 날짜 형식으로 추억 목록 조회를 시도하면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"2024.07.01", "2024-07", "2024", "a"})
    void cannotReadAllMemoryByInvalidDateFormat(String currentDate) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "올바르지 않은 쿼리 스트링 형식입니다.");

        // when & then
        mockMvc.perform(get("/memories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("currentDate", currentDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 특정 추억을 조회한다.")
    @Test
    void readMemory() throws Exception {
        // given
        long memoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        MemoryDetailResponse memoryDetailResponse = new MemoryDetailResponse(MemoryFixture.create(), List.of());
        when(memoryService.readMemoryById(anyLong(), any(Member.class))).thenReturn(memoryDetailResponse);

        // when & then
        mockMvc.perform(get("/memories/{memoryId}", memoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(memoryDetailResponse)));
    }

    @DisplayName("적합한 경로변수와 데이터를 통해 스타카토 수정에 성공한다.")
    @ParameterizedTest
    @MethodSource("memoryRequestProvider")
    void updateMemory(MemoryRequest memoryRequest) throws Exception {
        // given
        long memoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/memories/{memoryId}", memoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memoryRequest).getBytes()))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 추억을 수정할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidMemoryRequestProvider")
    void failUpdateMemory(MemoryRequest memoryRequest, String expectedMessage) throws Exception {
        // given
        long memoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(put("/memories/{memoryId}", memoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memoryRequest).getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 추억 수정에 실패한다.")
    @Test
    void failUpdateMemoryByInvalidPath() throws Exception {
        // given
        long memoryId = 0L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "추억 식별자는 양수로 이루어져야 합니다.");
        MemoryRequest memoryRequest = MemoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));

        // when & then
        mockMvc.perform(put("/memories/{memoryId}", memoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memoryRequest).getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 추억 식별자로 추억을 삭제한다.")
    @Test
    void deleteMemory() throws Exception {
        // given
        long memoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(delete("/memories/{memoryId}", memoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 추억 식별자로 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteMemoryByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "추억 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/memories/{memoryId}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
