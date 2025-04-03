package com.staccato.staccato.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.staccato.comment.domain.Comment;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 공유했을 때 응답 형식입니다.")
public record StaccatoSharedResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        long staccatoId,
        @Schema(example = SwaggerExamples.EXPIRED_AT)
        LocalDateTime expiredAt,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @ArraySchema(arraySchema = @Schema(example = SwaggerExamples.IMAGE_URLS))
        List<String> staccatoImageUrls,
        @Schema(example = SwaggerExamples.STACCATO_TITLE)
        String staccatoTitle,
        @Schema(example = SwaggerExamples.STACCATO_PLACE_NAME)
        String placeName,
        @Schema(example = SwaggerExamples.STACCATO_ADDRESS)
        String address,
        @Schema(example = SwaggerExamples.STACCATO_VISITED_AT)
        LocalDateTime visitedAt,
        @Schema(example = SwaggerExamples.FEELING)
        String feeling,
        List<CommentShareResponse> comments
) {
    public StaccatoSharedResponse(LocalDateTime expiredAt, Staccato staccato, Member member, List<StaccatoImage> staccatoImages, List<Comment> comments) {
        this(
                staccato.getId(),
                expiredAt,
                member.getNickname().getNickname(),
                toStaccatoImageUrls(staccatoImages),
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
