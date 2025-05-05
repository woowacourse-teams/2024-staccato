package com.staccato.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.category.service.dto.request.CategoryRequestV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryRequestV3Fixtures;
import com.staccato.fixture.member.MemberFixtures;

class CategoryControllerV3Test extends ControllerTest {

    static Stream<CategoryRequestV3> categoryRequestProvider() {
        return Stream.of(
                CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                        .withTerm(LocalDate.of(2024, 1, 1),
                                LocalDate.of(2024, 12, 31)).build(),
                CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                        .withTerm(null, null).build(),
                CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                        .withIsShared(false).build(),
                CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                        .withIsShared(true).build()
        );
    }

    static Stream<Arguments> invalidCategoryRequestProvider() {
        return Stream.of(
                Arguments.of(CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                                .withCategoryTitle(null).build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                                .withCategoryTitle("  ").build(),
                        "카테고리 제목을 입력해주세요."),
                Arguments.of(CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                                .withCategoryTitle("가".repeat(31)).build(),
                        "제목은 공백 포함 30자 이하로 설정해주세요."),
                Arguments.of(CategoryRequestV3Fixtures.defaultCategoryRequestV3()
                                .withDescription("가".repeat(501)).build(),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."),
                Arguments.of(CategoryRequestV3Fixtures.defaultCategoryRequestV3()
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
    void createCategoryWithoutOption(CategoryRequestV3 categoryRequest) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        when(categoryService.createCategory(any(), any())).thenReturn(new CategoryIdResponse(1));

        // when & then
        mockMvc.perform(post("/v3/categories")
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
    void failCreateCategory(CategoryRequestV3 categoryRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/v3/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
