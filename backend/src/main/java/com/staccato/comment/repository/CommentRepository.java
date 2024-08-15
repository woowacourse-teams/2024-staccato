package com.staccato.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
