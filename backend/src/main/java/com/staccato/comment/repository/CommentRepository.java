package com.staccato.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.staccato.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMomentId(long momentId);

    void deleteAllByMomentId(long momentId);
}
