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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequest;
import com.staccato.category.service.dto.request.CategoryStaccatoLocationRangeRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponseV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponseV3;
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponse;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryRequestFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

class CategoryControllerTest extends ControllerTest {

    static Stream<CategoryRequest> categoryRequestProvider() {
        return Stream.of(
                CategoryRequestFixtures.defaultCategoryRequest()
                        .withTerm(LocalDate.of(2024, 1, 1),
                                LocalDate.of(2024, 12, 31)).build(),
                CategoryRequestFixtures.defaultCategoryRequest()
                        .withTerm(null, null).build()
        );
    }

    static Stream<Arguments> invalidCategoryRequestProvider() {
        return Stream.of(
                Arguments.of(CategoryRequestFixtures.defaultCategoryRequest()
                                .withCategoryTitle(null).build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryRequestFixtures.defaultCategoryRequest()
                                .withCategoryTitle("  ").build(),
                        "카테고리 제목을 입력해주세요.")
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        String categoryRequest = """
                {
                    "categoryThumbnailUrl": "https://example.com/categories/geumohrm.jpg",
                    "categoryTitle": "2023 여름 휴가",
                    "description": "친구들과 함께한 여름 휴가 여행"
                }
                """;
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));

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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category categoryWithTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.PINK)
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        Category categoryWithoutTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.BLUE)
                .withTerm(null, null).build();
        CategoryResponsesV3 categoryResponses = new CategoryResponsesV3(List.of(
                new CategoryResponseV3(categoryWithTerm, 0),
                new CategoryResponseV3(categoryWithoutTerm, 0))
        );
        when(categoryService.readAllCategories(any(Member.class), any(CategoryReadRequest.class))).thenReturn(categoryResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle",
                            "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                            "startAt": "2024-01-01",
                            "endAt": "2024-12-31"
                        },
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle",
                            "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg"
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

    @DisplayName("유효하지 않은 필터링 조건은 무시하고, 모든 카테고리 목록을 조회한다.")
    @Test
    void readAllCategoryIgnoringInvalidFilter() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        Category category1 = CategoryFixtures.defaultCategory().build();
        Category category2 = CategoryFixtures.defaultCategory().build();
        CategoryResponsesV3 categoryResponses = new CategoryResponsesV3(List.of(
                new CategoryResponseV3(category1, 0),
                new CategoryResponseV3(category2, 0))
        );
        when(categoryService.readAllCategories(any(Member.class), any(CategoryReadRequest.class))).thenReturn(categoryResponses);

        // when & then
        mockMvc.perform(get("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("filters", "invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.size()").value(2));
    }

    @DisplayName("특정 날짜를 포함하고 있는 모든 카테고리 목록을 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readAllCandidateCategoriesIncludingDate() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().build();
        CategoryNameResponses categoryNameResponses = CategoryNameResponses.from(List.of(category));
        when(categoryService.readAllCategoriesByMemberAndDateAndPrivateFlag(any(Member.class), any(), any(Boolean.class))).thenReturn(categoryNameResponses);
        String expectedResponse = """
                {
                    "categories": [
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("specificDate", LocalDate.now().toString())
                        .param("isPrivate", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("specificDate 파라미터 없이 요청하면 예외가 발생한다.")
    @Test
    void cannotReadAllCandidateCategoriesWithoutSpecificDate() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "필수 요청 파라미터가 누락되었습니다. 필요한 파라미터를 제공해주세요.");

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("isPrivate", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("isPrivate 파라미터 없이 요청해도 기본값 false로 정상 동작한다")
    @Test
    void canReadAllCandidateCategoriesWithoutIsPrivate() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("specificDate", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }

    @DisplayName("잘못된 날짜 형식으로 카테고리 목록 조회를 시도하면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"2024.07.01", "2024-07", "2024", "a"})
    void cannotReadAllCandidateCategoriesByInvalidDateFormat(String currentDate) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "올바르지 않은 쿼리 스트링 형식입니다.");

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("specificDate", currentDate)
                        .param("isPrivate", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("잘못된 boolean 형식으로 카테고리 목록 조회를 시도하면 예외가 발생한다.")
    @Test
    void cannotReadAllCandidateCategoriesByInvalidBooleanFormat() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "올바르지 않은 쿼리 스트링 형식입니다.");

        // when & then
        mockMvc.perform(get("/categories/candidates")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("specificDate", LocalDate.now().toString())
                        .param("isPrivate", "invalidString"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 특정 카테고리를 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readCategory() throws Exception {
        // given
        long categoryId = 1;
        Member member = MemberFixtures.defaultMember().build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        Category category = CategoryFixtures.defaultCategory().withHost(member).build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        CategoryDetailResponseV3 categoryDetailResponse = new CategoryDetailResponseV3(category, List.of(staccato), member);
        when(categoryService.readCategoryById(anyLong(), any(Member.class))).thenReturn(categoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                    "categoryTitle": "categoryTitle",
                    "startAt": "2024-01-01",
                    "endAt": "2024-12-31",
                    "description": "categoryDescription",
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
        mockMvc.perform(get("/categories/{categoryId}", categoryId)
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
                .withHost(member)
                .build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        CategoryDetailResponseV3 categoryDetailResponse = new CategoryDetailResponseV3(category, List.of(staccato), member);
        when(categoryService.readCategoryById(anyLong(), any(Member.class))).thenReturn(categoryDetailResponse);
        String expectedResponse = """
                {
                    "categoryId": null,
                    "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                    "categoryTitle": "categoryTitle",
                    "description": "categoryDescription",
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
        mockMvc.perform(get("/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("특정 카테고리에 속한 스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccatoByCategory() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category category = CategoryFixtures.defaultCategory().withColor(Color.PINK).build();
        CategoryStaccatoLocationResponse response1 = new CategoryStaccatoLocationResponse(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(BigDecimal.ZERO, BigDecimal.ZERO).build()
        );
        CategoryStaccatoLocationResponse response2 = new CategoryStaccatoLocationResponse(
                StaccatoFixtures.defaultStaccato()
                        .withCategory(category)
                        .withSpot(new BigDecimal("80.456789"), new BigDecimal("123.456789")).build()
        );
        CategoryStaccatoLocationResponses responses = new CategoryStaccatoLocationResponses(List.of(response1, response2));

        when(categoryService.readAllStaccatoByCategory(any(Member.class), anyLong(), any(CategoryStaccatoLocationRangeRequest.class))).thenReturn(responses);
        String expectedResponse = """
                {
                    "categoryStaccatoLocationResponses": [
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 0,
                             "longitude": 0
                         },
                         {
                             "staccatoId": null,
                             "staccatoColor": "pink",
                             "latitude": 80.456789,
                             "longitude": 123.456789
                         }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/categories/1/staccatos")
                        .param("neLat", "81")
                        .param("neLng", "124")
                        .param("swLat", "80")
                        .param("swLng", "123")
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
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
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

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

    @DisplayName("사용자가 카테고리 식별자로 카테고리를 나간다.")
    @Test
    void exitCategory() throws Exception {
        // given
        long categoryId = 1;
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(delete("/categories/{categoryId}/members/me", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자가 잘못된 카테고리 식별자로 나가려고 하면 예외가 발생한다.")
    @Test
    void cannotExitCategoryByInvalidId() throws Exception {
        // given
        long invalidId = 0;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "카테고리 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/categories/{categoryId}/members/me", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
