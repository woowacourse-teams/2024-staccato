package com.staccato.comment.service;

import com.staccato.comment.service.dto.request.CommentRequestV2;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.category.domain.Category;
import com.staccato.moment.domain.Staccato;
import com.staccato.moment.repository.StaccatoRepository;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final StaccatoRepository staccatoRepository;

    @Transactional
    public long createComment(CommentRequestV2 commentRequest, Member member) {
        Staccato staccato = getStaccato(commentRequest.staccatoId());
        validateOwner(staccato.getCategory(), member);
        Comment comment = commentRequest.toComment(staccato, member);

        return commentRepository.save(comment).getId();
    }

    public CommentResponses readAllCommentsByStaccatoId(Member member, Long staccatoId) {
        Staccato staccato = getStaccato(staccatoId);
        validateOwner(staccato.getCategory(), member);
        List<Comment> comments = commentRepository.findAllByStaccatoId(staccatoId);
        sortByCreatedAtAscending(comments);

        return CommentResponses.from(comments);
    }

    private Staccato getStaccato(long momentId) {
        return staccatoRepository.findById(momentId)
            .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private void validateOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void sortByCreatedAtAscending(List<Comment> comments) {
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
    }

    @Transactional
    public void updateComment(Member member, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = getComment(commentId);
        validateCommentOwner(comment, member);
        comment.changeContent(commentUpdateRequest.content());
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new StaccatoException("요청하신 댓글을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteComment(long commentId, Member member) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            validateCommentOwner(comment, member);
            commentRepository.deleteById(commentId);
        });
    }

    private void validateCommentOwner(Comment comment, Member member) {
        if (comment.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
