package com.staccato.visit.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Visit", description = "Visit API")
public interface VisitControllerDocs {
    ResponseEntity<Void> createVisit(@Valid @RequestBody VisitRequest visitRequest);

    @Operation(summary = "특정 방문 기록 조회", description = "특정 방문 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 방문 기록 조회 성공", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = VisitDetailResponse.class))),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 조회하려는 방문 기록이 존재하지 않을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<VisitDetailResponse> readVisitById(
            @Parameter(example = "1", required = true) @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId);

    ResponseEntity<Void> deleteVisitById(
            @Parameter @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") Long visitId);
}
