package com.staccato.staccato.controller.docs;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Staccato V2", description = "Staccato API V2")
public interface StaccatoControllerV2Docs {
    @Operation(summary = "스타카토 목록 조회", description = "주어진 카테고리와 위경도 범위가 있다면, 그에 해당하는 모든 스타카토 목록을 조회하고, 조건이 없다면 모든 스타카토를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "스타카토 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                
                    (1) 쿼리 파라미터로 제공된 위도 또는 경도가 유효한 범위를 벗어난 경우
                    (2) 위도는 -90.0 이상 90.0 이하, 경도는 -180.0 이상 180.0 이하여야 함
                    (3) 잘못된 카테고리 식별자가 주어지는 경우
                    """,
                    responseCode = "400")
    })
    ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(
            @Parameter(hidden = true) Member member, @Validated @ModelAttribute StaccatoLocationRangeRequest staccatoLocationRangeRequest);

    @Operation(summary = "스타카토 조회", description = "스타카토를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "스타카토 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 조회하려는 스타카토가 존재하지 않을 때                
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<StaccatoDetailResponseV2> readStaccatoById(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "스타카토 ID", example = "1") @PathVariable @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId);
}
