package com.staccato.category.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;

import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequestV2;
import com.staccato.category.service.dto.request.CategoryUpdateRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponseV2;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryResponsesV2;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category V2", description = "Category API V2")
public interface CategoryControllerV2Docs {
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
            @Parameter(required = true) @Valid CategoryRequestV2 categoryRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "카테고리 목록 조회", description = "사용자의 모든 카테고리 목록을 조회합니다.")
    @ApiResponse(description = "카테고리 목록 조회 성공", responseCode = "200")
    ResponseEntity<CategoryResponsesV2> readAllCategories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "정렬 기준은 생략하거나 유효하지 않은 값에 대해서는 최근 수정 순(UPDATED)이 기본 정렬로 적용됩니다. 필터링 조건은 생략하거나 유효하지 않은 값이 들어오면 적용되지 않습니다.") CategoryReadRequest categoryReadRequest
    );

    @Operation(summary = "카테고리 조회", description = "사용자의 카테고리을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 존재하지 않는 카테고리을 조회하려고 했을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryDetailResponseV2> readCategory(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId);

    @Operation(summary = "카테고리 수정", description = "카테고리 정보(썸네일, 제목, 내용, 기간)를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(카테고리 제목)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때
                                        
                    (6) 변경하려는 카테고리 기간이 이미 존재하는 스타카토를 포함하지 않을 때
                                        
                    (7) 수정하려는 카테고리이 존재하지 않을 때
                                        
                    (8) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateCategory(
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Parameter(required = true) @Valid CategoryUpdateRequest categoryUpdateRequest,
            @Parameter(hidden = true) Member member);
}
