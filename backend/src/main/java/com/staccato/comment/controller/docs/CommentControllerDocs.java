package com.staccato.comment.controller.docs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;

import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comment", description = "Comment API")
public interface CommentControllerDocs {
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "댓글 생성 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 스타카토 식별자가 양수가 아닐 때
                                        
                    (2) 스타카토를 선택하지 않았을 때
                                        
                    (3) 요청한 스타카토를 찾을 수 없을 때
                                        
                    (4) 댓글 내용이 공백 뿐이거나 없을 때
                                        
                    (5) 댓글이 공백 포함 500자 초과일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> createComment(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "댓글 생성 시 요구 형식") @Valid CommentRequest commentRequest);

    @Operation(summary = "댓글 조회", description = "스타카토에 속한 모든 댓글을 생성 순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "댓글 조회 성공", responseCode = "200"),
            @ApiResponse(description = "스타카토 식별자가 양수가 아닐 때 발생", responseCode = "400"),
    })
    ResponseEntity<CommentResponses> readCommentsByMomentId(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "댓글이 속한 스타카토 식별자", example = "1") @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId);

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "댓글 수정 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 댓글 식별자가 양수가 아닐 때
                                        
                    (2) 요청한 댓글을 찾을 수 없을 때
                                        
                    (3) 댓글 내용이 공백 뿐이거나 없을 때
                                        
                    (4) 댓글이 공백 포함 500자 초과일 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<Void> updateComment(
            @Parameter(hidden = true) Member member,
            @Parameter(description = "댓글 식별자", example = "1") @Min(value = 1L, message = "댓글 식별자는 양수로 이루어져야 합니다.") long commentId,
            @Parameter(description = "댓글 수정 시 요구 형식") @Valid CommentUpdateRequest commentUpdateRequest);

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "댓글 삭제 성공", responseCode = "200"),
            @ApiResponse(description = "댓글 식별자가 양수가 아닐 시 댓글 삭제 실패", responseCode = "400")
    })
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 식별자", example = "1") @Min(value = 1L, message = "댓글 식별자는 양수로 이루어져야 합니다.") long commentId,
            @Parameter(hidden = true) Member member);
}
