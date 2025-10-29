package com.staccato.comment.controller.docs;

import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;

import com.staccato.comment.service.dto.response.CommentResponsesV2;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comment V2", description = "Comment API V2")
public interface CommentControllerV2Docs {

    @Operation(summary = "댓글 조회", description = "스타카토에 속한 모든 댓글을 생성 순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "댓글 조회 성공", responseCode = "200"),
            @ApiResponse(description = "스타카토 식별자가 양수가 아닐 때 발생", responseCode = "400"),
    })
    ResponseEntity<CommentResponsesV2> readCommentsByStaccatoId(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "댓글이 속한 스타카토 식별자", example = "1") @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId);
}
