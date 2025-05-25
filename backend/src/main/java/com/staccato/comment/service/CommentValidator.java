package com.staccato.comment.service;

import org.springframework.stereotype.Component;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    public Comment getCommentByIdOrThrow(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new StaccatoException("요청하신 댓글을 찾을 수 없어요."));
    }

    public void validateOwner(Comment comment, Member member) {
        if (comment.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
