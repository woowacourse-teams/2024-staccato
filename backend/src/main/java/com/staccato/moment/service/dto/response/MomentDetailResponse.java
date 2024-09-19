package com.staccato.moment.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 조회했을 때 응답 형식입니다.")
public record MomentDetailResponse(
        @Schema(example = "1")
        long momentId,
        @Schema(example = "1")
        long memoryId,
        @Schema(example = "2024 서울 투어")
        String memoryTitle,
        @Schema(example = "즐거웠던 남산에서의 기억")
        String staccatoTitle,
        @ArraySchema(arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        List<String> momentImageUrls,
        @Schema(example = "2021-11-08T11:58:20")
        LocalDateTime visitedAt,
        @Schema(example = "happy")
        String feeling,
        @Schema(example = "남산서울타워")
        String placeName,
        @Schema(example = "서울 용산구 남산공원길 105")
        String address,
        List<CommentResponse> comments
) {
    public MomentDetailResponse(Moment moment) {
        this(
                moment.getId(),
                moment.getMemory().getId(),
                moment.getMemory().getTitle(),
                moment.getTitle(),
                moment.getMomentImages().getImages().stream().map(MomentImage::getImageUrl).toList(),
                moment.getVisitedAt(),
                moment.getFeeling().getValue(),
                moment.getSpot().getPlaceName(),
                moment.getSpot().getAddress(),
                moment.getComments().stream().map(CommentResponse::new).toList()
        );
    }
}
