package com.staccato.travel.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;

import com.staccato.member.domain.Member;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Travel", description = "Travel API")
public interface TravelControllerDocs {
    ResponseEntity<Void> createTravel(@Valid TravelRequest travelRequest, Member member);

    @Operation(summary = "여행 상세 목록 조회", description = "사용자의 전체/년도별 여행 상세 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "여행 상세 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = "입력받은 년도가 유효하지 않을 때 발생", responseCode = "400")
    })
    ResponseEntity<TravelResponses> readAllTravels(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "조회할 년도") Integer year);

    @Operation(summary = "특정 여행 상세 조회", description = "사용자의 특정 여행 상세를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 여행 상세 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 존재하지 않는 여행을 조회하려고 했을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<TravelDetailResponse> readTravel(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "여행 상세 ID") @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") Long travelId);

    @Operation(summary = "여행 상세 수정", description = "여행 상세 정보(썸네일, 제목, 내용, 기간)를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "여행 상세 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(여행 제목, 기간)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때
                                        
                    (6) 변경하려는 여행 기간이 이미 존재하는 방문 기록을 포함하지 않을 때
                                        
                    (7) 수정하려는 여행이 존재하지 않을 때
                                        
                    (8) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateTravel(
            @Parameter(description = "여행 상세 ID") @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") Long travelId,
            @Valid TravelRequest travelRequest,
            @Parameter(hidden = true) Member member);

    ResponseEntity<Void> deleteTravel(
            @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.") Long travelId,
            Member member);
}
