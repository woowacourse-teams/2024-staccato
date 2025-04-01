package com.staccato.staccato.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 공유했을 때 응답 형식입니다.")
public record StaccatoSharedResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "2024-09-30T17:00:00")
        LocalDateTime expiredAt,
        @Schema(example = "staccato")
        String nickname,
        @ArraySchema(arraySchema = @Schema(example = "[" +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg\"]"))
        List<String> staccatoImageUrls,
        @Schema(example = "귀여운 스타카토 키링")
        String staccatoTitle,
        @Schema(example = "한국 루터회관 8층")
        String placeName,
        @Schema(example = "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층")
        String address,
        @Schema(example = "2024-09-29T17:00:00")
        LocalDateTime visitedAt,
        @Schema(example = "scared")
        String feeling,
        List<CommentShareResponse> comments
) {
    public StaccatoSharedResponse(LocalDateTime expiredAt, Staccato staccato, Member member, List<Comment> comments) {
        this(
                staccato.getId(),
                expiredAt,
                member.getNickname().getNickname(),
                toStaccatoImageUrls(staccato.getStaccatoImages().getImages()),
                staccato.getTitle(),
                staccato.getSpot().getPlaceName(),
                staccato.getSpot().getAddress(),
                staccato.getVisitedAt(),
                staccato.getFeeling().getValue(),
                toCommentShareResponses(comments)
        );
    }

    private static List<String> toStaccatoImageUrls(List<StaccatoImage> staccatoImages) {
        return staccatoImages.stream()
                .map(StaccatoImage::getImageUrl)
                .toList();
    }

    private static List<CommentShareResponse> toCommentShareResponses(List<Comment> comments) {
        return comments.stream()
                .map(CommentShareResponse::new)
                .toList();
    }
}
