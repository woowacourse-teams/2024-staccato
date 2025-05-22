package com.staccato.invitation.service.dto.response;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.member.domain.Member;

public record CategoryInvitationCreateResponse(
        Long inviteeId,
        String statusCode,
        String message,
        @JsonInclude(Include.NON_NULL)
        Long invitationId
) {
    public static CategoryInvitationCreateResponse success(CategoryInvitation categoryInvitation) {
        return new CategoryInvitationCreateResponse(
                categoryInvitation.getInvitee().getId(),
                HttpStatus.OK.toString(),
                "초대 요청에 성공하였습니다.",
                categoryInvitation.getId()
        );
    }

    public static CategoryInvitationCreateResponse fail(Member invitee, String message) {
        return new CategoryInvitationCreateResponse(invitee.getId(), HttpStatus.BAD_REQUEST.toString(), message, null);
    }

    public boolean isOk() {
        return statusCode.equals(HttpStatus.OK.toString());
    }
}
