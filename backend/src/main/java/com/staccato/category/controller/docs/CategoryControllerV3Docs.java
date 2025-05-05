package com.staccato.category.controller.docs;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.staccato.category.service.dto.request.CategoryRequestV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category V3", description = "Category API V3")
public interface CategoryControllerV3Docs {
    @Operation(summary = "카테고리 생성", description = "카테고리(썸네일, 제목, 내용, 기간)을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 생성 성공", responseCode = "201"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>

                    (1) 필수 값(카테고리 제목, 색상)이 누락되었을 때

                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때

                    (3) 제목이 공백 포함 30자를 초과했을 때

                    (4) 내용이 공백 포함 500자를 초과했을 때

                    (5) 기간 설정이 잘못되었을 때 (시작 날짜와 끝날짜 중 하나만 설정할 수 없음)

                    (6) 이미 존재하는 카테고리 이름일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryIdResponse> createCategory(
            @Parameter(required = true) @Valid CategoryRequestV3 categoryRequest,
            @Parameter(hidden = true) Member member);
}
