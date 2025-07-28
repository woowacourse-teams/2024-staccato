package com.staccato.category.controller.docs;

import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.staccato.category.service.dto.request.CategoryColorRequest;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequest;
import com.staccato.category.service.dto.request.CategoryStaccatoLocationRangeRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponse;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponses;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.category.service.dto.response.CategoryStaccatoResponses;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category", description = "Category API")
public interface CategoryControllerDocs {
    @Operation(summary = "카테고리 생성", deprecated = true, description = "카테고리(썸네일, 제목, 내용, 기간)을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 생성 성공", responseCode = "201"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 필수 값(카테고리 제목)이 누락되었을 때
                    
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                    
                    (3) 제목이 공백 포함 30자를 초과했을 때
                    
                    (4) 내용이 공백 포함 500자를 초과했을 때
                    
                    (5) 기간 설정이 잘못되었을 때 (시작 날짜와 끝날짜 중 하나만 설정할 수 없음)
                    
                    (6) 이미 존재하는 카테고리 이름일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryIdResponse> createCategory(
            @Parameter(required = true) @Valid CategoryRequest categoryRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "카테고리 목록 조회", deprecated = true, description = "사용자의 모든 카테고리 목록을 조회합니다.")
    @ApiResponse(description = "카테고리 목록 조회 성공", responseCode = "200")
    ResponseEntity<CategoryResponses> readAllCategories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "정렬 기준은 생략하거나 유효하지 않은 값에 대해서는 최근 수정 순(UPDATED)이 기본 정렬로 적용됩니다. 필터링 조건은 생략하거나 유효하지 않은 값이 들어오면 적용되지 않습니다.") CategoryReadRequest categoryReadRequest
    );

    @Operation(summary = "특정 날짜를 포함하는 사용자의 개인/전체 카테고리 목록 조회", description = "특정 날짜를 포함하는 사용자의 개인/전체 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 입력받은 특정 날짜가 유효하지 않을 때
                    
                    (2) 입력받은 개인 카테고리 여부가 유효하지 않을 때
                    """, responseCode = "400")
    })
    ResponseEntity<CategoryNameResponses> readAllCandidateCategories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "특정 날짜 (yyyy-MM-dd 형식)", example = "2024-08-21", required = true)
            @RequestParam(required = true)
            LocalDate specificDate,
            @Parameter(description = "개인 카테고리 여부 (기본값: false)", example = "true", schema = @Schema(defaultValue = "false"))
            @RequestParam(required = false, defaultValue = "false")
            boolean isPrivate
    );

    @Operation(summary = "카테고리 조회", deprecated = true, description = "사용자의 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 존재하지 않는 카테고리를 조회하려고 했을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryDetailResponse> readCategory(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId);

    @Operation(summary = "카테고리 내 스타카토 위치 목록 조회", description = "사용자의 카테고리 내 스타카토 위치 목록을 조회합니다. 위경도 기준 범위 기반 조회를 제공합니다")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 내 스타카토 위치 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 존재하지 않는 카테고리을 조회하려고 했을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    
                    (3) 위도 또는 경도 값이 유효한 범위를 벗어났을 때 (위도: -90.0 이상 90.0 이하, 경도: -180.0 이상 180.0 이하)
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryStaccatoLocationResponses> readStaccatoLocationsByCategory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Validated @ModelAttribute CategoryStaccatoLocationRangeRequest categoryStaccatoLocationRangeRequest
    );

    @Operation(summary = "카테고리 내 스타카토 목록 조회", description = "사용자의 카테고리 내 스타카토 목록을 조회합니다.")
    @ApiResponse(description = "스타카토 목록 조회 성공", responseCode = "200")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 존재하지 않는 카테고리을 조회하려고 했을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryStaccatoResponses> readStaccatosByCategory(
            @Parameter(hidden = true) Member member,
            @PathVariable @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Parameter(description = """
                    다음 페이지를 위한 커서, 요청 시 주어진 값이 없다면 첫 페이지를 조회합니다.
                    커서는 구분자(|)를 기준으로 Base64로 인코딩하여 `id|visitedAt`순으로 값을 구성합니다.
                    """, example = SwaggerExamples.PAGINATION_CURSOR) String cursor,
            @Parameter(description = "조회할 데이터 수(기본: 10, 최소: 1, 최대: 100)", example = SwaggerExamples.PAGINATION_LIMIT)
            @Min(value = 1, message = "limit는 1 이상, 100 이하여야 합니다.")
            @Max(value = 100, message = "limit는 1 이상, 100 이하여야 합니다.")
            int limit
    );

    @Operation(summary = "카테고리 수정", deprecated = true, description = "카테고리 정보(썸네일, 제목, 내용, 기간)를 수정합니다.")
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
            @Parameter(required = true) @Valid CategoryRequest categoryRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "카테고리 색상 수정", description = "카테고리 색상을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 색상 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 필수 값(카테고리 색상)이 누락되었을 때
                    
                    (2) 색상 설정이 잘못되었을 때
                    
                    (3) 수정하려는 카테고리이 존재하지 않을 때
                    
                    (4) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateCategoryColor(
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Parameter(required = true) @Valid CategoryColorRequest categoryColorRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "카테고리 삭제", description = "사용자의 카테고리를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 삭제 성공", responseCode = "200"),
            @ApiResponse(description = "Path Variable 형식이 잘못되었을 때 발생", responseCode = "400")
    })
    ResponseEntity<Void> deleteCategory(
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "카테고리 나가기", description = "공동카테고리에서 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 나가기 성공", responseCode = "200"),
            @ApiResponse(description = "Path Variable 형식이 잘못되었을 때 발생", responseCode = "400")
    })
    ResponseEntity<Void> deleteSelfFromCategory(
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId,
            @Parameter(hidden = true) Member member);
}
