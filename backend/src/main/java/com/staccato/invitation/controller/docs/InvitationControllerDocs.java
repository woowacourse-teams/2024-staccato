package com.staccato.invitation.controller.docs;

import org.springframework.http.ResponseEntity;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface InvitationControllerDocs {
    @Operation(summary = "카테고리 멤버 초대", description = "지정된 카테고리에 다른 멤버들을 초대하면, 각 멤버 초대에 대한 성공/실패 결과를 배열로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 초대 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 초대하려는 카테고리가 존재하지 않을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<InvitationResultResponses> inviteMembers(
            @Parameter(hidden = true) Member member,
            @Parameter(required = true) CategoryInvitationRequest categoryInvitationRequest
    );
}
