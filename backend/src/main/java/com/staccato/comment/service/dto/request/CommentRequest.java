package com.staccato.comment.service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 생성 시 요청 형식입니다.")
public record CommentRequest(
        @Schema(example = "1")
        @NotNull(message = "순간을 선택해주세요.")
        @Min(value = 1L, message = "순간 식별자는 양수로 이루어져야 합니다.")
        Long momentId,
        @Schema(example = "예시 댓글 내용")
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(min = 1, max = 500, message = "댓글은 공백 포함 500자 이하로 입력해주세요.")
        String content
) {
        public Comment toComment(Moment moment, Member member) {
                return Comment.builder()
                        .content(content)
                        .moment(moment)
                        .member(member)
                        .build();
        }
}
