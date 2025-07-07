package com.staccato.member.controller.docs;

import org.springframework.http.ResponseEntity;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberProfileResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member V2", description = "Member API V2")
public interface MyPageControllerV2Docs {
    @Operation(summary = "사용자 프로필 조회", description = "프로필 상 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "사용자 정보 조회 성공", responseCode = "200"),
            @ApiResponse(description = "요청 형식이 잘못 되었을 때", responseCode = "400")
    })
    public ResponseEntity<MemberProfileResponseV2> readMyPage(@Parameter(hidden = true) @LoginMember Member member);
}
