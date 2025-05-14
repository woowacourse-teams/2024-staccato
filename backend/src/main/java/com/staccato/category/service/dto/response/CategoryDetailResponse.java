package com.staccato.category.service.dto.response;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 대한 응답 형식입니다.")
public record CategoryDetailResponse(
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
    @Schema(example = SwaggerExamples.CATEGORY_START_AT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate startAt,
    @Schema(example = SwaggerExamples.CATEGORY_END_AT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate endAt,
    List<MemberResponse> mates,
    List<StaccatoResponse> staccatos
) {

  public CategoryDetailResponse(Category category, List<Staccato> staccatos) {
    this(
        category.getId(),
        category.getThumbnailUrl(),
        category.getTitle(),
        category.getDescription(),
        category.getTerm().getStartAt(),
        category.getTerm().getEndAt(),
        toMemberResponses(category),
        toStaccatoResponses(staccatos)
    );
  }

  private static List<MemberResponse> toMemberResponses(Category category) {
    return category.getCategoryMembers().stream()
            .map(CategoryMember::getMember)
            .map(MemberResponse::new).toList();
  }

  private static List<StaccatoResponse> toStaccatoResponses(List<Staccato> staccatos) {
    return staccatos.stream().map(StaccatoResponse::new).toList();
  }
}
