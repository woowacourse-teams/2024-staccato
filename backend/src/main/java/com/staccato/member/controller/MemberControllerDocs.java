package com.staccato.member.controller;

import org.springframework.http.ResponseEntity;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberResponses;
import com.staccato.member.service.dto.response.MemberSearchResponses;
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
    ResponseEntity<MemberSearchResponses> readMembersByNickname(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "검색어와 검색 결과에서 제외할 카테고리 식별자로 필터링합니다. 검색어가 없다면 빈 배열을 반환합니다.") MemberReadRequest memberReadRequest
    );
}
