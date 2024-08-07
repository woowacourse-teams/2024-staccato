package com.staccato.visit.controller.docs;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.member.domain.Member;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitIdResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Visit", description = "Visit API")
public interface VisitControllerDocs {
    @Operation(summary = "방문 기록 생성", description = "방문 기록을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "방문 기록 생성 성공", responseCode = "201"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(사진을 제외한 모든 값)이 누락되었을 때
                                        
                    (2) 존재하지 않는 travelId일 때
                                        
                    (3) 올바르지 않은 날짜 형식일 때
                                        
                    (4) 사진이 5장을 초과했을 때
                                        
                    (5) 방문 날짜가 여행 기간에 포함되지 않을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<VisitIdResponse> createVisit(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "key = data", required = true) @Valid VisitRequest visitRequest,
            @Parameter(description = "key = visitImageFiles") @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") List<MultipartFile> visitImagesFile
    );

    @Operation(summary = "특정 방문 기록 조회", description = "특정 방문 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 방문 기록 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 조회하려는 방문 기록이 존재하지 않을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<VisitDetailResponse> readVisitById(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "방문 기록 ID", example = "1", required = true) @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId);

    @Operation(summary = "특정 방문 기록 수정", description = "특정 방문 기록을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 방문 기록 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 조회하려는 방문 기록이 존재하지 않을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                                        
                    (3) 사진의 총 갯수가 5장을 초과하였을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateVisitById(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "방문 기록 ID", example = "1", required = true) @PathVariable @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId,
            @Parameter(description = "key = visitImageFiles") @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") List<MultipartFile> visitImageFiles,
            @Parameter(description = "key = data", required = true) @Valid VisitUpdateRequest request);

    @Operation(summary = "방문 기록 삭제", description = "방문 기록을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "방문 기록 삭제에 성공했거나 해당 방문 기록이 존재하지 않는 경우", responseCode = "200"),
            @ApiResponse(description = "방문 기록 식별자에 양수가 아닌 값을 기입했을 경우", responseCode = "400")
    })
    ResponseEntity<Void> deleteVisitById(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "방문 기록 ID", example = "1", required = true) @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId
    );
}
