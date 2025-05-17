package com.staccato.invitation.service.dto;

import java.util.List;
import jakarta.validation.constraints.Min;
import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.staccato.config.swagger.SwaggerExamples.MEMBER_IDS;

@Schema(description = "카테고리에 함께하는 사람 초대를 위한 요청 형식입니다. 이 요청은 지정된 카테고리로 멤버들을 초대할 때 사용됩니다.")
public record CategoryInvitationRequest(
        @ArraySchema(
                arraySchema = @Schema(
                        description = "초대할 멤버 식별자 목록",
                        example = MEMBER_IDS
                ))
        List<Long> memberIds
) {
}
