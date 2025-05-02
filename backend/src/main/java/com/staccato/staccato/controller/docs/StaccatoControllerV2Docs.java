package com.staccato.staccato.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Staccato V2", description = "Staccato API V2")
public interface StaccatoControllerV2Docs {
    @Operation(summary = "스타카토 목록 조회", description = "스타카토 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "스타카토 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
            <발생 가능한 케이스>
            
            (1) 쿼리 파라미터로 제공된 위도 또는 경도가 유효한 범위를 벗어난 경우
            (2) 위도는 -90.0 이상 90.0 이하, 경도는 -180.0 이상 180.0 이하여야 함
            """,
                    responseCode = "400")
    })
    ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(
            @Parameter(hidden = true) Member member, @Validated @ModelAttribute StaccatoLocationRangeRequest staccatoLocationRangeRequest);
}
