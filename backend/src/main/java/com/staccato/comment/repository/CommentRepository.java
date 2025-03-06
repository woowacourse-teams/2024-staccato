package com.staccato.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMomentId(long momentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.moment.id IN :momentIds")
    void deleteAllByMomentIdInBulk(@Param("momentIds") List<Long> momentIds);
}
