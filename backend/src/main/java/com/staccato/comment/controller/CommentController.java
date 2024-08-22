package com.staccato.comment.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.comment.controller.docs.CommentControllerDocs;
import com.staccato.comment.service.CommentService;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController implements CommentControllerDocs {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @LoginMember Member member,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        long commentId = commentService.createComment(commentRequest, member);
        return ResponseEntity.created(URI.create("/comments/" + commentId))
                .build();
    }

    @GetMapping
    public ResponseEntity<CommentResponses> readCommentsByMomentId(
            @LoginMember Member member,
            @RequestParam @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long momentId
    ) {
        CommentResponses commentResponses = commentService.readAllCommentsByMomentId(member, momentId);
        return ResponseEntity.ok().body(commentResponses);
    }

    @PutMapping
    public ResponseEntity<Void> updateComment(
            @LoginMember Member member,
            @RequestParam @Min(value = 1L, message = "댓글 식별자는 양수로 이루어져야 합니다.") long commentId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest
    ) {
        commentService.updateComment(member, commentId, commentUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteComment(
            @RequestParam @Min(value = 1L, message = "댓글 식별자는 양수로 이루어져야 합니다.") long commentId,
            @LoginMember Member member
    ) {
        commentService.deleteComment(commentId, member);
        return ResponseEntity.ok().build();
    }
}
