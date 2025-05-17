package com.staccato.invitation.service.dto;

import java.util.Set;
import jakarta.validation.constraints.Min;
import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.staccato.config.swagger.SwaggerExamples.MEMBER_IDS;

@Schema(description = "카테고리에 함께하는 사람 초대를 위한 요청 형식입니다. 이 요청은 지정된 카테고리로 멤버들을 초대할 때 사용됩니다.")
public record CategoryInvitationRequest(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        @Min(value = 1L, message = "카테고리 식별자는 양수로 이루어져야 합니다.")
        Long categoryId,
        @ArraySchema(
                arraySchema = @Schema(
                        description = "초대할 멤버 식별자 목록, 중복 값은 무시",
                        example = MEMBER_IDS
                ))
        Set<Long> memberIds
) {
}
