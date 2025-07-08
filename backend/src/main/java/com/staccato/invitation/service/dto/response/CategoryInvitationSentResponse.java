package com.staccato.invitation.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.invitation.domain.CategoryInvitation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 초대 목록에서 각 초대의 응답 형식입니다.")
public record CategoryInvitationSentResponse(
        @Schema(example = SwaggerExamples.INVITATION_ID)
        Long invitationId,
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long inviteeId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String inviteeNickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String inviteeProfileImageUrl,
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle
) {
    public CategoryInvitationSentResponse(CategoryInvitation categoryInvitation) {
        this(
                categoryInvitation.getId(),
                categoryInvitation.getInvitee().getId(),
                categoryInvitation.getInvitee().getNickname().getNickname(),
                categoryInvitation.getInvitee().getImageUrl(),
                categoryInvitation.getCategory().getId(),
                categoryInvitation.getCategory().getTitle().getTitle()
        );
    }
}
