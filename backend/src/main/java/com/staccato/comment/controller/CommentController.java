package com.staccato.comment.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.comment.service.CommentService;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
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
}
