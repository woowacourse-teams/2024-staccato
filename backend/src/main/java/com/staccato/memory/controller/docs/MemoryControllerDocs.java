package com.staccato.memory.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.member.domain.Member;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Memory", description = "Memory API")
public interface MemoryControllerDocs {
    @Operation(summary = "추억 상세 생성", description = "추억 상세(썸네일, 제목, 내용, 기간)를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 상세 생성 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(추억 제목, 기간)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<MemoryIdResponse> createMemory(
            @Parameter(description = "key = data") @Valid MemoryRequest memoryRequest,
            @Parameter(description = "key = memoryThumbnailFile") MultipartFile memoryThumbnailFile,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "추억 상세 목록 조회", description = "사용자의 전체/년도별 추억 상세 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 상세 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = "입력받은 년도가 유효하지 않을 때 발생", responseCode = "400")
    })
    ResponseEntity<MemoryResponses> readAllMemories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "조회할 년도", example = "2024") Integer year);

    @Operation(summary = "특정 추억 상세 조회", description = "사용자의 특정 추억 상세를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 추억 상세 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 존재하지 않는 추억을 조회하려고 했을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<MemoryDetailResponse> readMemory(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "추억 상세 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId);

    @Operation(summary = "추억 상세 수정", description = "추억 상세 정보(썸네일, 제목, 내용, 기간)를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 상세 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(추억 제목, 기간)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때
                                        
                    (6) 변경하려는 추억 기간이 이미 존재하는 순간 기록을 포함하지 않을 때
                                        
                    (7) 수정하려는 추억이 존재하지 않을 때
                                        
                    (8) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateMemory(
            @Parameter(description = "추억 상세 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Parameter(description = "key = data", required = true) @Valid MemoryRequest memoryRequest,
            @Parameter(description = "key = memoryThumbnailFile") MultipartFile memoryThumbnailFile,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "특정 추억 상세 삭제", description = "사용자의 특정 추억 상세를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "특정 추억 상세 삭제 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 순간 기록이 존재하는 추억 상세를 삭제하려고 했을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> deleteMemory(
            @Parameter(description = "추억 상세 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Parameter(hidden = true) Member member);
}
