package com.staccato.category.service.dto.response;

import com.staccato.category.domain.Category;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 대한 응답 형식입니다.")
public record CategoryDetailResponse(
    @Schema(example = "1")
    Long categoryId,
    @Schema(example = "https://example.com/categorys/geumohrm.jpg")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String categoryThumbnailUrl,
    @Schema(example = "런던 여행")
    String categoryTitle,
    @Schema(example = "런던 시내 탐방")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description,
    @Schema(example = "2024-07-27")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate startAt,
    @Schema(example = "2024-07-29")
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
    return category.getMates().stream().map(MemberResponse::new).toList();
  }

  private static List<StaccatoResponse> toStaccatoResponses(List<Staccato> staccatos) {
    return staccatos.stream().map(StaccatoResponse::new).toList();
  }
}
