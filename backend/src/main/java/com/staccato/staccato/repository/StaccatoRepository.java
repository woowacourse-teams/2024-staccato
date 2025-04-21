package com.staccato.staccato.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public interface StaccatoRepository extends JpaRepository<Staccato, Long> {
    @Query("SELECT s FROM Staccato s WHERE s.category.id = :categoryId order by s.visitedAt desc, s.createdAt desc")
    List<Staccato> findAllByCategoryIdOrdered(long categoryId);

    List<Staccato> findAllByCategory_CategoryMembers_Member(Member member);

    List<Staccato> findAllByCategoryId(long categoryId);

    List<Staccato> findAllByCategoryIdIn(List<Long> categoryIds);

    @Modifying
    @Query("DELETE FROM Staccato s WHERE s.category.id = :categoryId")
    void deleteAllByCategoryIdInBulk(@Param("categoryId") Long categoryId);
}
