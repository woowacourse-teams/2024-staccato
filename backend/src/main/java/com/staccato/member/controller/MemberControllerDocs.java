package com.staccato.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member", description = "Member API")
public interface MemberControllerDocs {
    @Operation(summary = "닉네임으로 사용자 검색", description = "닉네임으로 검색하여 본인 외 사용자 목록을 조회합니다. 함께하는 사용자 초대 목적으로 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 초대 성공", responseCode = "200")
    })
    public ResponseEntity<MemberResponses> readMembersByNickname(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "검색어", example = "닉네임") @RequestParam(value = "nickname") String nickname
    );
}
