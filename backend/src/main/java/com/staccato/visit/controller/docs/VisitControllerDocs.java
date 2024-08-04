package com.staccato.visit.controller.docs;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitIdResponse;

import io.swagger.v3.oas.annotations.Operation;
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
            @Valid VisitRequest visitRequest,
            @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.") List<MultipartFile> visitImagesFile
    );

    @Operation(summary = "방문 기록 삭제", description = "방문 기록을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "방문 기록 삭제에 성공했거나 해당 방문 기록이 존재하지 않는 경우", responseCode = "200"),
            @ApiResponse(description = "방문 기록 식별자에 양수가 아닌 값을 기입했을 경우", responseCode = "400"),
            @ApiResponse(description = "삭제할 권한이 없는 경우", responseCode = "403")
    })
    ResponseEntity<Void> deleteVisitById(
            @Min(value = 1L, message = "방문 기록 식별자는 양수로 이루어져야 합니다.") long visitId
    );
}
