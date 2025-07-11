package com.staccato.comment.controller;

import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.comment.controller.docs.CommentControllerV2Docs;
import com.staccato.comment.service.CommentService;
import com.staccato.comment.service.dto.response.CommentResponsesV2;
import com.staccato.config.auth.LoginMember;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequestMapping("/v2/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerV2 implements CommentControllerV2Docs {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentResponsesV2> readCommentsByStaccatoId(
            @LoginMember Member member,
            @RequestParam @Min(value = 1L, message = "스타카토 식별자는 양수로 이루어져야 합니다.") long staccatoId
    ) {
        CommentResponsesV2 commentResponses = commentService.readAllCommentsByStaccatoId(member, staccatoId);
        return ResponseEntity.ok().body(commentResponses);
    }
}
