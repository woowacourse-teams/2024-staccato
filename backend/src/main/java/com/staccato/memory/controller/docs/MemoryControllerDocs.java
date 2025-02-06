package com.staccato.memory.controller.docs;

import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import com.staccato.member.domain.Member;
import com.staccato.memory.service.dto.request.MemoryReadRequest;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Memory", description = "Memory API")
public interface MemoryControllerDocs {
    @Operation(summary = "추억 생성", description = "추억(썸네일, 제목, 내용, 기간)을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 생성 성공", responseCode = "201"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(추억 제목)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때 (시작 날짜와 끝날짜 중 하나만 설정할 수 없음)
                                        
                    (6) 이미 존재하는 추억 이름일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<MemoryIdResponse> createMemory(
            @Parameter(required = true) @Valid MemoryRequest memoryRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "추억 목록 조회", description = "사용자의 모든 추억 목록을 조회합니다.")
    @ApiResponse(description = "추억 목록 조회 성공", responseCode = "200")
    ResponseEntity<MemoryResponses> readAllMemories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "정렬 기준은 생략하거나 유효하지 않은 값에 대해서는 최근 수정 순(UPDATED)이 기본 정렬로 적용됩니다. 필터링 조건은 생략하거나 유효하지 않은 값이 들어오면 적용되지 않습니다.") MemoryReadRequest memoryReadRequest
    );

    @Operation(summary = "특정 날짜를 포함하는 사용자의 모든 추억 목록 조회", description = "특정 날짜를 포함하는 사용자의 모든 추억 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 목록 조회 성공", responseCode = "200"),
            @ApiResponse(description = "입력받은 현재 날짜가 유효하지 않을 때 발생", responseCode = "400")
    })
    ResponseEntity<MemoryNameResponses> readAllCandidateMemories(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "현재 날짜", example = "2024-08-21") LocalDate currentDate);

    @Operation(summary = "추억 조회", description = "사용자의 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 조회 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 존재하지 않는 추억을 조회하려고 했을 때
                                        
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<MemoryDetailResponse> readMemory(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "추억 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId);

    @Operation(summary = "추억 수정", description = "추억 정보(썸네일, 제목, 내용, 기간)를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 필수 값(추억 제목)이 누락되었을 때
                                        
                    (2) 날짜 형식(yyyy-MM-dd)이 잘못되었을 때
                                        
                    (3) 제목이 공백 포함 30자를 초과했을 때
                                        
                    (4) 내용이 공백 포함 500자를 초과했을 때
                                        
                    (5) 기간 설정이 잘못되었을 때
                                        
                    (6) 변경하려는 추억 기간이 이미 존재하는 스타카토를 포함하지 않을 때
                                        
                    (7) 수정하려는 추억이 존재하지 않을 때
                                        
                    (8) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateMemory(
            @Parameter(description = "추억 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Parameter(required = true) @Valid MemoryRequest memoryRequest,
            @Parameter(hidden = true) Member member);

    @Operation(summary = "추억 삭제", description = "사용자의 추억을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "추억 삭제 성공", responseCode = "200"),
            @ApiResponse(description = "Path Variable 형식이 잘못되었을 때 발생", responseCode = "400")
    })
    ResponseEntity<Void> deleteMemory(
            @Parameter(description = "추억 ID", example = "1") @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Parameter(hidden = true) Member member);
}
