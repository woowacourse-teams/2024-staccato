package com.staccato.invitation.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.invitation.domain.CategoryInvitation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "받은 초대 목록에서 각 초대의 응답 형식입니다.")
public record CategoryInvitationReceivedResponse(
        @Schema(example = SwaggerExamples.INVITATION_ID)
        Long invitationId,
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long inviterId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String inviterNickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String inviterProfileImageUrl,
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle
) {
    public CategoryInvitationReceivedResponse(CategoryInvitation categoryInvitation) {
        this(
                categoryInvitation.getId(),
                categoryInvitation.getInviter().getId(),
                categoryInvitation.getInviter().getNickname().getNickname(),
                categoryInvitation.getInviter().getImageUrl(),
                categoryInvitation.getCategory().getId(),
                categoryInvitation.getCategory().getTitle().getTitle()
        );
    }
}
