package com.staccato.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authorization", description = "Authorization API")
public interface AuthControllerDocs {
    @Operation(summary = "등록 및 로그인", description = "애플리케이션을 최초 실행할 때 한 번만 닉네임 입력을 받고, 식별 코드를 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "등록 및 로그인 성공", responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 이미 존재하는 닉네임일 때
                                        
                    (2) 닉네임의 형식이 잘못되었을 때 (한글, 영어, 마침표(.), 언더바(_)만 사용 가능)
                                        
                    (3) 닉네임이 20자를 초과하였을 때
                                        
                    (4) 닉네임을 입력하지 않았을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<LoginResponse> login(@Valid LoginRequest loginRequest);
}
