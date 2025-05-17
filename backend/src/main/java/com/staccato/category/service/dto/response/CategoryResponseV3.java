package com.staccato.category.service.dto.response;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.service.dto.response.MemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryResponseV3(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        String categoryColor,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt,
        @Schema(example = SwaggerExamples.CATEGORY_IS_SHARED)
        boolean isShared,
        List<MemberResponse> members,
        @Schema(example = SwaggerExamples.STACCATO_COUNT)
        Long staccatoCount
) {
    public CategoryResponseV3(Category category, long staccatoCount) {
        this(
                category.getId(),
                category.getThumbnailUrl(),
                category.getTitle(),
                category.getColor().getName(),
                category.getTerm().getStartAt(),
                category.getTerm().getEndAt(),
                category.getIsShared(),
                toMemberResponses(category.getCategoryMembers()),
                staccatoCount
        );
    }

    private static List<MemberResponse> toMemberResponses(List<CategoryMember> categoryMembers) {
        return categoryMembers.stream()
                .map(CategoryMember::getMember)
                .map(MemberResponse::new)
                .toList();
    }

    public CategoryResponse toCategoryResponse() {
        return new CategoryResponse(
                categoryId,
                categoryThumbnailUrl,
                categoryTitle,
                startAt,
                endAt
        );
    }

    public CategoryResponseV2 toCategoryResponseV2() {
        return new CategoryResponseV2(
                categoryId,
                categoryThumbnailUrl,
                categoryTitle,
                categoryColor,
                startAt,
                endAt
        );
    }
}
