package com.staccato.category.service.dto.request;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 후보 목록 조회시 특정 날짜, 개인/공유 카테고리 flag 값 기준 조회를 위한 요청 형식입니다.")
public record CategoryCandidateRequest(
        @Parameter(description = "특정 날짜", example = "2024-08-21")
        LocalDate specificDate,
        @Parameter(description = "공유 카테고리 flag 값", example = "false")
        boolean isShared) {
}
