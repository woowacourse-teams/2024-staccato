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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.memory.MemoryNameResponsesFixture;
import com.staccato.fixture.memory.MemoryRequestFixture;
import com.staccato.fixture.memory.MemoryResponsesFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.dto.request.CategoryRequest;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemoryService memoryService;
    @MockBean
    private AuthService authService;

    static Stream<CategoryRequest> categoryRequestProvider() {
        return Stream.of(
                new CategoryRequest(null, "2023 여름 휴가", "친구들과 함께한 여름 휴가 카테고리", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new CategoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", null, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new CategoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", null, null, null)
        );
    }

    static Stream<Arguments> invalidCategoryRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new CategoryRequest("https://example.com/memorys/geumohrm.jpg", null, "친구들과 함께한 여름 휴가 카테고리", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "카테고리 제목을 입력해주세요."
                ),
                Arguments.of(
                        new CategoryRequest("https://example.com/memorys/geumohrm.jpg", "  ", "친구들과 함께한 여름 휴가 카테고리", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "카테고리 제목을 입력해주세요."
                ),
                Arguments.of(
                        new CategoryRequest("https://example.com/memorys/geumohrm.jpg", "가".repeat(31), "친구들과 함께한 여름 휴가 카테고리", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "제목은 공백 포함 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new CategoryRequest("https://example.com/memorys/geumohrm.jpg", "2023 여름 휴가", "가".repeat(501), LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."
                )
        );
    }

    @DisplayName("카테고리를 생성하는 요청/응답의 역직렬화/직렬화에 성공한다.")
    @Test
    void createCategory() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        String categoryRequest = """
                {
                    "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg",
                    "categoryTitle": "2023 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 여행",
                    "startAt": "2023-07-01",
                    "endAt": "2023-07-10"
                }
                """;
        when(memoryService.createMemory(any(), any())).thenReturn(new MemoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/categories/1"))
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("기간이 없는 카테고리를 생성하는 요청/응답의 역직렬화/직렬화에 성공한다.")
    @Test
    void createCategoryWithoutTerm() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        String categoryRequest = """
                {
                    "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg",
                    "categoryTitle": "2023 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 여행"
                }
                """;
        when(memoryService.createMemory(any(), any())).thenReturn(new MemoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/categories/1"))
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("사용자가 선택적으로 카테고리 정보를 입력하면, 새로운 카테고리를 생성한다.")
    @ParameterizedTest
    @MethodSource("categoryRequestProvider")
    void createCategoryWithoutOption(CategoryRequest categoryRequest) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        when(memoryService.createMemory(any(), any())).thenReturn(new MemoryIdResponse(1));

        // when & then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/categories/1"))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 카테고리를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCategoryRequestProvider")
    void failCreateCategory(CategoryRequest categoryRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 모든 카테고리 목록을 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readAllCategory() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        Memory memory = MemoryFixture.create(MemberFixture.create());
        MemoryResponses memoryResponses = MemoryResponsesFixture.create(memory);
        when(memoryService.readAllMemories(any(Member.class))).thenReturn(memoryResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "2024 여름 휴가",
                            "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg",
                            "startAt": "2024-07-01",
                            "endAt": "2024-07-10"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("사용자가 기간이 없는 카테고리를 포함한 목록을 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readAllCategoryWithoutTerm() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        Memory memory = MemoryFixture.create(MemberFixture.create());
        MemoryResponses memoryResponses = MemoryResponsesFixture.create(memory);
        when(memoryService.readAllMemories(any(Member.class))).thenReturn(memoryResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "2024 여름 휴가",
                            "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("특정 날짜를 포함하고 있는 모든 카테고리 목록을 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readAllCategoryIncludingDate() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        Memory memory = MemoryFixture.create(MemberFixture.create());
        MemoryNameResponses memoryNameResponses = MemoryNameResponsesFixture.create(memory);
        when(memoryService.readAllMemoriesIncludingDate(any(Member.class), any())).thenReturn(memoryNameResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "2024 여름 휴가"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("currentDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("잘못된 날짜 형식으로 카테고리 목록 조회를 시도하면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"2024.07.01", "2024-07", "2024", "a"})
    void cannotReadAllCategoryByInvalidDateFormat(String currentDate) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "올바르지 않은 쿼리 스트링 형식입니다.");

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("currentDate", currentDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 특정 카테고리를 조회하는 응답 직렬화에 한다.")
    @Test
    void readCategory() throws Exception {
        // given
        long categoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        Memory memory = MemoryFixture.create(MemberFixture.create());
        MomentResponse momentResponse = new MomentResponse(MomentFixture.create(memory), "image.jpg");
        MemoryDetailResponse memoryDetailResponse = new MemoryDetailResponse(memory, List.of(momentResponse));
        when(memoryService.readMemoryById(anyLong(), any(Member.class))).thenReturn(memoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg",
                    "categoryTitle": "2024 여름 휴가",
                    "startAt": "2024-07-01",
                    "endAt": "2024-07-10",
                    "description": "친구들과 함께한 여름 휴가 추억",
                    "mates": [
                        {
                            "memberId": null,
                            "nickname": "staccato"
                        }
                    ],
                    "staccatos": [
                            {
                                "staccatoId": null,
                                "staccatoTitle": "staccatoTitle",
                                "staccatoImageUrl": "image.jpg",
                                "visitedAt": "2024-07-01T10:00:00"
                            }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


    @DisplayName("사용자가 기간이 없는 특정 카테고리를 조회하는 응답 직렬화에 한다.")
    @Test
    void readCategoryWithoutTerm() throws Exception {
        // given
        long categoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        Memory memory = MemoryFixture.create(null, null, MemberFixture.create());
        MomentResponse momentResponse = new MomentResponse(MomentFixture.create(memory), "image.jpg");
        MemoryDetailResponse memoryDetailResponse = new MemoryDetailResponse(memory, List.of(momentResponse));
        when(memoryService.readMemoryById(anyLong(), any(Member.class))).thenReturn(memoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/memorys/geumohrm.jpg",
                    "categoryTitle": "2024 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 추억",
                    "mates": [
                        {
                            "memberId": null,
                            "nickname": "staccato"
                        }
                    ],
                    "staccatos": [
                            {
                                "staccatoId": null,
                                "staccatoTitle": "staccatoTitle",
                                "staccatoImageUrl": "image.jpg",
                                "visitedAt": "2024-07-01T10:00:00"
                            }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("적합한 경로변수와 데이터를 통해 스타카토 수정에 성공한다.")
    @ParameterizedTest
    @MethodSource("categoryRequestProvider")
    void updateCategory(CategoryRequest categoryRequest) throws Exception {
        // given
        long categoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 카테고리를 수정할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCategoryRequestProvider")
    void failUpdateCategory(CategoryRequest categoryRequest, String expectedMessage) throws Exception {
        // given
        long categoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 카테고리 수정에 실패한다.")
    @ParameterizedTest
    @MethodSource("categoryRequestProvider")
    void failUpdateCategoryByInvalidPath(CategoryRequest categoryRequest) throws Exception {
        // given
        long categoryId = 0L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "카테고리 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 카테고리 식별자로 카테고리를 삭제한다.")
    @Test
    void deleteCategory() throws Exception {
        // given
        long categoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(delete("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 카테고리 식별자로 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteCategoryByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "카테고리 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/categories/{categoryId}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
