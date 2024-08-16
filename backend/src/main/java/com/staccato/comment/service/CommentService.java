package com.staccato.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.Memory;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MomentRepository momentRepository;

    @Transactional
    public long createComment(CommentRequest commentRequest, Member member) {
        Moment moment = getMoment(commentRequest.momentId());
        validateOwner(moment.getMemory(), member);
        Comment comment = commentRequest.toComment(moment, member);

        return commentRepository.save(comment).getId();
    }

    public CommentResponses readAllCommentsByMomentId(Member member, Long momentId) {
        Moment moment = getMoment(momentId);
        validateOwner(moment.getMemory(), member);
        List<Comment> comments = commentRepository.findAllByMomentIdOrderByCreatedAtAsc(momentId);

        return CommentResponses.from(comments);
    }

    @Transactional
    public void updateComment(Member member, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = getComment(commentId);
        validateCommentOwner(comment, member);
        comment.changeContent(commentUpdateRequest.content());
    }

    private Moment getMoment(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new StaccatoException("요청하신 순간을 찾을 수 없어요."));
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new StaccatoException("요청하신 댓글을 찾을 수 없어요."));
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void validateCommentOwner(Comment comment, Member member) {
        if (comment.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    @Transactional
    public void deleteComment(long commentId, Member member) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            validateCommentOwner(comment, member);
            commentRepository.deleteById(commentId);
        });
    }
}
