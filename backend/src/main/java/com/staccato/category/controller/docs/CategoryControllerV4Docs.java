package com.staccato.category.controller.docs;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import com.staccato.category.service.dto.response.CategoryDetailResponseV4;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category V4", description = "Category API V4")
public interface CategoryControllerV4Docs {
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
    ResponseEntity<CategoryDetailResponseV4> readCategory(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "카테고리 ID", example = "1") @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.") long categoryId);
}
