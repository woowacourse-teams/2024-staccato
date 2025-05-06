package com.staccato.category.controller;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequestV2;
import com.staccato.category.service.dto.response.CategoryDetailResponseV2;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryResponsesV2;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryRequestV2Fixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

class CategoryControllerV2Test extends ControllerTest {

    static Stream<CategoryRequestV2> categoryRequestProvider() {
        return Stream.of(
                CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                        .withTerm(LocalDate.of(2024, 1, 1),
                                LocalDate.of(2024, 12, 31)).build(),
                CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                        .withTerm(null, null).build()
        );
    }

    static Stream<Arguments> invalidCategoryRequestProvider() {
        return Stream.of(
                Arguments.of(CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                                .withCategoryTitle(null).build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                                .withCategoryTitle("  ").build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                                .withCategoryTitle("가".repeat(31)).build(),
                        "제목은 공백 포함 30자 이하로 설정해주세요."),
                Arguments.of(CategoryRequestV2Fixtures.defaultCategoryRequestV2()
                                .withDescription("가".repeat(501)).build(),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        );
    }

    @DisplayName("카테고리를 생성하는 요청/응답의 역직렬화/직렬화에 성공한다.")
    @Test
    void createCategory() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        String categoryRequest = """
                {
                    "categoryThumbnailUrl": "https://example.com/categories/geumohrm.jpg",
                    "categoryTitle": "2023 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 여행",
                    "categoryColor": "gray",
                    "startAt": "2023-07-01",
                    "endAt": "2023-07-10"
                }
                """;
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/v2/categories")
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        String categoryRequest = """
                {
                    "categoryThumbnailUrl": "https://example.com/categories/geumohrm.jpg",
                    "categoryTitle": "2023 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 여행",
                    "categoryColor": "gray"
                }
                """;
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/v2/categories")
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
    void createCategoryWithoutOption(CategoryRequestV2 categoryRequest) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));

        // when & then
        mockMvc.perform(post("/v2/categories")
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
    void failCreateCategory(CategoryRequestV2 categoryRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/v2/categories")
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category categoryWithTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.PINK)
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        Category categoryWithoutTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.BLUE)
                .withTerm(null, null).build();
        CategoryResponsesV2 categoryResponses = CategoryResponsesV2.from(List.of(categoryWithTerm, categoryWithoutTerm));
        when(categoryService.readAllCategories(any(Member.class), any(CategoryReadRequest.class))).thenReturn(categoryResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle",
                            "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                            "categoryColor": "pink",
                            "startAt": "2024-01-01",
                            "endAt": "2024-12-31"
                        },
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle",
                            "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                            "categoryColor": "blue"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("유효하지 않은 필터링 조건은 무시하고, 모든 카테고리 목록을 조회한다.")
    @Test
    void readAllCategoryIgnoringInvalidFilter() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        Category category1 = CategoryFixtures.defaultCategory().build();
        Category category2 = CategoryFixtures.defaultCategory().build();
        CategoryResponsesV2 categoryResponses = CategoryResponsesV2.from(List.of(category1, category2));

        when(categoryService.readAllCategories(any(Member.class), any(CategoryReadRequest.class))).thenReturn(categoryResponses);

        // when & then
        mockMvc.perform(get("/v2/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("filters", "invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.size()").value(2));
    }

    @DisplayName("사용자가 특정 카테고리를 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readCategory() throws Exception {
        // given
        long categoryId = 1;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        Category category = CategoryFixtures.defaultCategory().buildWithMember(member);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        CategoryDetailResponseV2 categoryDetailResponse = new CategoryDetailResponseV2(category, List.of(staccato));
        when(categoryService.readCategoryById(anyLong(), any(Member.class))).thenReturn(categoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                    "categoryTitle": "categoryTitle",
                    "description": "categoryDescription",
                    "categoryColor": "pink",
                    "startAt": "2024-01-01",
                    "endAt": "2024-12-31",
                    "mates": [
                        {
                            "memberId": null,
                            "nickname": "nickname",
                            "memberImageUrl": "https://example.com/memberImage.png"
                        }
                    ],
                    "staccatos": [
                            {
                                "staccatoId": null,
                                "staccatoTitle": "staccatoTitle",
                                "staccatoImageUrl": "https://example.com/staccatoImage.jpg",
                                "visitedAt": "2024-06-01T00:00:00"
                            }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


    @DisplayName("사용자가 기간이 없는 특정 카테고리를 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readCategoryWithoutTerm() throws Exception {
        // given
        long categoryId = 1;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        Category category = CategoryFixtures.defaultCategory()
                .withTerm(null, null)
                .buildWithMember(member);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        CategoryDetailResponseV2 categoryDetailResponse = new CategoryDetailResponseV2(category, List.of(staccato));
        when(categoryService.readCategoryById(anyLong(), any(Member.class))).thenReturn(categoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                    "categoryTitle": "categoryTitle",
                    "description": "categoryDescription",
                    "categoryColor": "pink",
                    "mates": [
                        {
                            "memberId": null,
                            "nickname": "nickname",
                            "memberImageUrl": "https://example.com/memberImage.png"
                        }
                    ],
                    "staccatos": [
                            {
                                "staccatoId": null,
                                "staccatoTitle": "staccatoTitle",
                                "staccatoImageUrl": "https://example.com/staccatoImage.jpg",
                                "visitedAt": "2024-06-01T00:00:00"
                            }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("적합한 경로변수와 데이터를 통해 스타카토 수정에 성공한다.")
    @ParameterizedTest
    @MethodSource("categoryRequestProvider")
    void updateCategory(CategoryRequestV2 categoryRequest) throws Exception {
        // given
        long categoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(put("/v2/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 카테고리를 수정할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCategoryRequestProvider")
    void failUpdateCategory(CategoryRequestV2 categoryRequest, String expectedMessage) throws Exception {
        // given
        long categoryId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(put("/v2/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 카테고리 수정에 실패한다.")
    @ParameterizedTest
    @MethodSource("categoryRequestProvider")
    void failUpdateCategoryByInvalidPath(CategoryRequestV2 categoryRequest) throws Exception {
        // given
        long categoryId = 0L;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "카테고리 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(put("/v2/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
