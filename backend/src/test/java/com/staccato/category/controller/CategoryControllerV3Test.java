package com.staccato.category.controller;

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
import com.staccato.category.service.dto.request.CategoryCreateRequest;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponseV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryResponseV3;
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryCreateRequestFixtures;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerV3Test extends ControllerTest {

    static Stream<CategoryCreateRequest> categoryRequestProvider() {
        return Stream.of(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withTerm(LocalDate.of(2024, 1, 1),
                                LocalDate.of(2024, 12, 31)).build(),
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withTerm(null, null).build(),
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withIsShared(false).build(),
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withIsShared(true).build()
        );
    }

    static Stream<Arguments> invalidCategoryRequestProvider() {
        return Stream.of(
                Arguments.of(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                                .withCategoryTitle(null).build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                                .withCategoryTitle("  ").build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                                .withIsShared(null).build(),
                        "카테고리 공개 여부를 입력해주세요.")
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
                    "endAt": "2023-07-10",
                    "isShared": "false"
                }
                """;
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/v3/categories")
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
                    "categoryColor": "gray",
                    "isShared": "false"
                }
                """;
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));
        String expectedResponse = """
                {
                    "categoryId" : 1
                }
                """;

        // when & then
        mockMvc.perform(post("/v3/categories")
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
    void createCategoryWithoutOption(CategoryCreateRequest categoryCreateRequest) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));

        // when & then
        mockMvc.perform(post("/v3/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/categories/1"))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @DisplayName("사용자가 잘못된 형식으로 정보를 입력하면, 카테고리를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCategoryRequestProvider")
    void failCreateCategory(CategoryCreateRequest categoryCreateRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/v3/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("사용자가 특정 카테고리를 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readCategory() throws Exception {
        // given
        long categoryId = 1;
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        when(authService.extractFromToken(anyString())).thenReturn(host);
        Category category = CategoryFixtures.defaultCategory().withHost(host).withGuests(List.of(guest)).build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage.jpg")).build();
        CategoryDetailResponseV3 categoryDetailResponse = new CategoryDetailResponseV3(category, List.of(staccato), host);
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
                    "isShared": true,
                    "members": [
                        {
                            "memberId": null,
                            "nickname": "host",
                            "memberImageUrl": "https://example.com/memberImage.png",
                            "memberRole": "host"
                        },
                        {
                            "memberId": null,
                            "nickname": "guest",
                            "memberImageUrl": "https://example.com/memberImage.png",
                            "memberRole": "guest"
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
        mockMvc.perform(get("/v3/categories/{categoryId}", categoryId)
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
                    "categoryColor": "pink",
                    "myRole": "host",
                    "members": [
                        {
                            "memberId": null,
                            "nickname": "nickname",
                            "memberImageUrl": "https://example.com/memberImage.png",
                            "memberRole": "host"
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
        mockMvc.perform(get("/v3/categories/{categoryId}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("사용자가 모든 카테고리 목록을 조회하는 응답 직렬화에 성공한다.")
    @Test
    void readAllCategory() throws Exception {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").build();
        Member guest3 = MemberFixtures.defaultMember().withNickname("guest3").build();
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        Category categoryWithTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.PINK)
                .withHost(host)
                .withGuests(List.of(guest, guest2, guest3))
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31)).build();
        Category categoryWithoutTerm = CategoryFixtures.defaultCategory()
                .withColor(Color.BLUE)
                .withHost(host)
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
                            "categoryColor": "pink",
                            "startAt": "2024-01-01",
                            "endAt": "2024-12-31",
                            "isShared": true,
                            "totalMemberCount": 4,
                            "members": [
                                {
                                    "memberId": null,
                                    "nickname": "host",
                                    "memberImageUrl": "https://example.com/memberImage.png"
                                },
                                {
                                    "memberId": null,
                                    "nickname": "guest",
                                    "memberImageUrl": "https://example.com/memberImage.png"
                                },
                                {
                                    "memberId": null,
                                    "nickname": "guest2",
                                    "memberImageUrl": "https://example.com/memberImage.png"
                                }
                            ],
                            "staccatoCount": 0
                        },
                        {
                            "categoryId": null,
                            "categoryTitle": "categoryTitle",
                            "categoryThumbnailUrl": "https://example.com/categoryThumbnail.jpg",
                            "categoryColor": "blue",
                            "isShared": false,
                            "totalMemberCount": 1,
                            "members": [
                                {
                                    "memberId": null,
                                    "nickname": "host",
                                    "memberImageUrl": "https://example.com/memberImage.png"
                                }
                            ],
                            "staccatoCount": 0
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v3/categories")
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
        mockMvc.perform(get("/v3/categories")
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("filters", "invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.size()").value(2));
    }
}
