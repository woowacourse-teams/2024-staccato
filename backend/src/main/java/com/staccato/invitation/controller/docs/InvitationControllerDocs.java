package com.staccato.invitation.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import com.staccato.config.auth.LoginMember;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationCreateResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponses;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Invitation", description = "Invitation API")
public interface InvitationControllerDocs {
    @Operation(summary = "카테고리 멤버 초대", description = "지정된 카테고리에 다른 멤버들을 초대합니다. 한 명이라도 실패 시 모두 실패 처리되며, 그 중 하나의 예외 메시지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "카테고리 초대 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 초대하려는 카테고리가 존재하지 않을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    
                    (3) 이미 카테고리에 추가된 사용자에게 초대 요청 했을 때
                    
                    (4) 이미 초대 요청을 보낸 사용자에게 초대 요청 했을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<CategoryInvitationCreateResponses> inviteMembers(
            @Parameter(hidden = true) Member member,
            @Parameter(required = true) @Valid CategoryInvitationRequest categoryInvitationRequest
    );

    @Operation(summary = "초대 요청 취소", description = "사용자의 초대 요청을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "초대 취소 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 취소하려는 초대가 존재하지 않을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    
                    (3) 이미 수락/거절된 초대 요청일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> cancelInvitation(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "초대 ID", example = "1") @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") long invitationId
    );

    @Operation(summary = "사용자가 요청한 초대 목록 조회", description = "특정 사용자가 보낸 모든 요청 상태의 초대 목록을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "보낸 초대 목록 조회 성공", responseCode = "200")
    })
    public ResponseEntity<CategoryInvitationRequestedResponses> readRequestedInvitations(
            @Parameter(hidden = true) @LoginMember Member member);

    @Operation(summary = "초대 요청 수락", description = "사용자의 초대 요청을 수락합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "초대 수락 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 수락하려는 초대가 존재하지 않을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    
                    (3) 이미 취소/거절된 초대 요청일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> acceptInvitation(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "초대 ID", example = "1") @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") long invitationId
    );

    @Operation(summary = "초대 요청 거절", description = "사용자의 초대 요청을 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "초대 거절 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                    
                    (1) 거절하려는 초대가 존재하지 않을 때
                    
                    (2) Path Variable 형식이 잘못되었을 때
                    
                    (3) 이미 취소/수락된 초대 요청일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> rejectInvitation(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "초대 ID", example = "1") @Min(value = 1L, message = "초대 식별자는 양수로 이루어져야 합니다.") long invitationId
    );
}
