package com.staccato.auth.controller.docs;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authorization", description = "Authorization API")
public interface AuthControllerDocs {
    @Operation(summary = "등록 및 로그인", deprecated = true, description = "애플리케이션을 최초 실행할 때 한 번만 닉네임 입력을 받고, 식별 코드를 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "등록 및 로그인 성공", responseCode = "200"),
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

    @Operation(summary = "고유 코드로 이전 기록 불러오기", deprecated = true, description = "사용자에게 발급된 고유 코드로 사용자를 조회하여 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "고유 코드로 사용자 조회 및 토큰 발급 성공", responseCode = "200"),
            @ApiResponse(description = "유효하지 않은 고유 코드일 때", responseCode = "400")
    })
    ResponseEntity<LoginResponse> loginByCode(@Parameter(description = "사용자에게 발급된 고유 코드") @RequestParam(name = "code") String code);
}
