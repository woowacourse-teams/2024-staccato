package com.staccato.invitation.service.dto.response;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record CategoryInvitationCreateResponses(List<CategoryInvitationCreateResponse> invitationResults) {
    public HttpStatusCode statusCode() {
        if (isAllBadRequest()) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    private boolean isAllBadRequest() {
        return invitationResults.stream()
                .noneMatch(CategoryInvitationCreateResponse::isOk);
    }
}
