package com.staccato.invitation.service.dto.response;

import org.springframework.http.HttpStatus;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.member.domain.Member;

public record InvitationResultResponse(
        Long inviteeId,
        String statusCode,
        String message,
        Long invitationId
) {
    public static InvitationResultResponse success(CategoryInvitation categoryInvitation) {
        return new InvitationResultResponse(
                categoryInvitation.getInvitee().getId(),
                HttpStatus.OK.toString(),
                "초대 요청에 성공하였습니다.",
                categoryInvitation.getId()
        );
    }

    public static InvitationResultResponse fail(Member invitee, String message) {
        return new InvitationResultResponse(invitee.getId(), HttpStatus.BAD_REQUEST.toString(), message, null);
    }
}
