package com.staccato.category.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import com.staccato.category.domain.Color;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 색상 변경을 위한 요청 형식입니다.")
public record CategoryColorRequest (
        @Schema(example = "pink")
        @NotBlank(message = "카테고리 색상을 입력해주세요.")
        String color
) {
    public Color toColor() {
        return Color.findByName(color);
    }
}
