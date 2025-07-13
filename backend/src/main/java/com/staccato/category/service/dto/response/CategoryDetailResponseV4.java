package com.staccato.category.service.dto.response;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리(스타카토 목록 외)에 대한 응답 형식입니다.")
public record CategoryDetailResponseV4(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_DESCRIPTION)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryColor,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt,
        @Schema(example = SwaggerExamples.CATEGORY_IS_SHARED)
        boolean isShared,
        @Schema(example = SwaggerExamples.CATEGORY_ROLE)
        String myRole,
        List<MemberDetailResponse> members
) {
    private static final int PRIORITY_HOST = 0;
    private static final int PRIORITY_CURRENT_USER = 1;
    private static final int PRIORITY_OTHERS = 2;

    public CategoryDetailResponseV4(Category category, Member member) {
        this(
                category.getId(),
                category.getThumbnailUrl(),
                category.getTitle().getTitle(),
                category.getDescription().getDescription(),
                category.getColor().getName(),
                category.getTerm().getStartAt(),
                category.getTerm().getEndAt(),
                category.getIsShared(),
                category.getRoleOfMember(member).getRole(),
                toMemberDetailResponses(category, member)
        );
    }

    private static List<MemberDetailResponse> toMemberDetailResponses(Category category, Member currentMember) {
        return category.getCategoryMembers().stream()
                .sorted(byHostFirstThenCurrentUser(currentMember).thenComparing(CategoryMember::getCreatedAt))
                .map(MemberDetailResponse::new)
                .toList();
    }

    private static Comparator<CategoryMember> byHostFirstThenCurrentUser(Member currentMember) {
        return Comparator.comparing((CategoryMember cm) -> {
            if (cm.isHost())
                return PRIORITY_HOST;
            if (cm.isMember(currentMember))
                return PRIORITY_CURRENT_USER;
            return PRIORITY_OTHERS;
        });
    }
}

