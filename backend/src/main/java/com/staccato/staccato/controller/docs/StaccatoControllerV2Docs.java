package com.staccato.staccato.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import com.staccato.member.domain.Member;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoIdResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Staccato", description = "Staccato API V2")
public interface StaccatoControllerV2Docs {
    @Operation(summary = "스타카토 목록 조회", description = "스타카토 목록을 조회합니다.")
    @ApiResponse(description = "스타카토 목록 조회 성공", responseCode = "200")
    ResponseEntity<StaccatoLocationResponsesV2> readAllStaccato(@Parameter(hidden = true) Member member);
}
