package com.staccato.moment.service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.staccato.moment.domain.MomentImages;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 수정 시 요청 형식입니다.")
public record MomentUpdateRequest(
        @Schema(example = "남산 서울타워")
        @NotNull(message = "스타카토 제목을 입력해주세요.")
        String staccatoTitle,
        @ArraySchema(
                arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        @Size(max = 5, message = "사진은 5장까지만 추가할 수 있어요.")
        List<String> momentImageUrls) {

    public MomentImages toMomentImages() {
        return new MomentImages(momentImageUrls);
    }
}
