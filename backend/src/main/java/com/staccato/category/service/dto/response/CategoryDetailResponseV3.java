package com.staccato.category.service.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.Category;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.staccato.domain.Staccato;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 대한 응답 형식입니다.")
public record CategoryDetailResponseV3(
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
        List<MemberDetailResponse> members,
        List<StaccatoResponse> staccatos
) {

    public CategoryDetailResponseV3(Category category, List<Staccato> staccatos) {
        this(
                category.getId(),
                category.getThumbnailUrl(),
                category.getTitle(),
                category.getDescription(),
                category.getColor().getName(),
                category.getTerm().getStartAt(),
                category.getTerm().getEndAt(),
                category.getIsShared(),
                toMemberDetailResponses(category),
                toStaccatoResponses(staccatos)
        );
    }

    private static List<MemberDetailResponse> toMemberDetailResponses(Category category) {
        return category.getCategoryMembers().stream()
                .map(MemberDetailResponse::new)
                .toList();
    }

    private static List<StaccatoResponse> toStaccatoResponses(List<Staccato> staccatos) {
        return staccatos.stream().map(StaccatoResponse::new).toList();
    }

    public CategoryDetailResponse toCategoryDetailResponse() {
        return new CategoryDetailResponse(
                categoryId,
                categoryThumbnailUrl,
                categoryTitle,
                description,
                startAt,
                endAt,
                toMemberResponses(members),
                staccatos
        );
    }

    public CategoryDetailResponseV2 toCategoryDetailResponseV2() {
        return new CategoryDetailResponseV2(
                categoryId,
                categoryThumbnailUrl,
                categoryTitle,
                description,
                categoryColor,
                startAt,
                endAt,
                toMemberResponses(members),
                staccatos
        );
    }

    private List<MemberResponse> toMemberResponses(List<MemberDetailResponse> mates) {
        List<MemberResponse> memberResponses = new ArrayList<>();
        for (MemberDetailResponse mate : mates) {
            memberResponses.add(new MemberResponse(mate.memberId(), mate.nickname(), mate.memberImageUrl()));
        }
        return memberResponses;
    }
}

