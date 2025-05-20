package com.staccato.invitation.service.dto.response;

import com.staccato.category.domain.Category;
import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;

public record InvitedCategoryResponse(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long id,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String title
) {
    public InvitedCategoryResponse(Category category) {
        this(category.getId(), category.getTitle());
    }
}
