package com.staccato.comment.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.event.CommentCreatedEvent;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final StaccatoRepository staccatoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public long createComment(CommentRequest commentRequest, Member member) {
        Staccato staccato = getStaccato(commentRequest.staccatoId());
        staccato.validateOwner(member);
        Comment comment = commentRequest.toComment(staccato, member);
        eventPublisher.publishEvent(new CommentCreatedEvent(member, staccato.getCategory(), comment));
        return commentRepository.save(comment).getId();
    }

    public CommentResponses readAllCommentsByStaccatoId(Member member, Long staccatoId) {
        Staccato staccato = getStaccato(staccatoId);
        staccato.validateOwner(member);
        List<Comment> comments = commentRepository.findAllByStaccatoId(staccatoId);
        sortByCreatedAtAscending(comments);

        return CommentResponses.from(comments);
    }

    private Staccato getStaccato(long staccatoId) {
        return staccatoRepository.findById(staccatoId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private void sortByCreatedAtAscending(List<Comment> comments) {
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
    }

    @Transactional
    public void updateComment(Member member, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = getComment(commentId);
        comment.validateOwner(member);
        comment.changeContent(commentUpdateRequest.content());
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new StaccatoException("요청하신 댓글을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteComment(long commentId, Member member) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            comment.validateOwner(member);
            commentRepository.deleteById(commentId);
        });
    }
}
