package com.staccato.comment.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.service.CategoryValidator;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.StaccatoValidator;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CategoryValidator categoryValidator;
    private final StaccatoValidator staccatoValidator;
    private final CommentValidator commentValidator;

    @Transactional
    public long createComment(CommentRequest commentRequest, Member member) {
        Staccato staccato = staccatoValidator.getStaccatoByIdOrThrow(commentRequest.staccatoId());
        categoryValidator.validateOwner(staccato.getCategory(), member);
        Comment comment = commentRequest.toComment(staccato, member);

        return commentRepository.save(comment).getId();
    }

    public CommentResponses readAllCommentsByStaccatoId(Member member, Long staccatoId) {
        Staccato staccato = staccatoValidator.getStaccatoByIdOrThrow(staccatoId);
        categoryValidator.validateOwner(staccato.getCategory(), member);
        List<Comment> comments = commentRepository.findAllByStaccatoId(staccatoId);
        sortByCreatedAtAscending(comments);

        return CommentResponses.from(comments);
    }

    private void sortByCreatedAtAscending(List<Comment> comments) {
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
    }

    @Transactional
    public void updateComment(Member member, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentValidator.getCommentByIdOrThrow(commentId);
        commentValidator.validateOwner(comment, member);
        comment.changeContent(commentUpdateRequest.content());
    }

    @Transactional
    public void deleteComment(long commentId, Member member) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            commentValidator.validateOwner(comment, member);
            commentRepository.deleteById(commentId);
        });
    }
}
