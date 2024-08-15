package com.staccato.moment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.moment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
