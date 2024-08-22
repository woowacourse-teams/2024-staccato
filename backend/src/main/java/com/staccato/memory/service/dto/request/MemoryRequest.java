package com.staccato.memory.service.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억을 생성/수정하기 위한 요청 형식입니다.")
public record MemoryRequest(
        @Schema(example = "http://example.com/london.png")
        String memoryThumbnailUrl,
        @Schema(example = "런던 추억")
        @NotBlank(message = "추억 제목을 입력해주세요.")
        @Size(min = 1, max = 30, message = "제목은 공백 포함 1자 이상 30자 이하로 설정해주세요.")
        String memoryTitle,
        @Schema(example = "런던 시내 탐방")
        @Size(max = 500, message = "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        String description,
        @Schema(example = "2024-07-27")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public MemoryRequest {
        if (Objects.nonNull(memoryTitle)) {
            memoryTitle = memoryTitle.trim();
        }
    }

    public Memory toMemory() {
        return Memory.builder()
                .thumbnailUrl(memoryThumbnailUrl)
                .title(memoryTitle)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
