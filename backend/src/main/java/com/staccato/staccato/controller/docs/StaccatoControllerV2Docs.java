package com.staccato.staccato.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.dto.request.ReadStaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Staccato V2", description = "Staccato API V2")
public interface StaccatoControllerV2Docs {
    @Operation(summary = "스타카토 목록 조회", description = "스타카토 목록을 조회합니다.")
    @ApiResponse(description = "스타카토 목록 조회 성공", responseCode = "200")
    ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(@Parameter(hidden = true) Member member, @ModelAttribute ReadStaccatoRequest readStaccatoRequest);
}
