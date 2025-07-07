package com.staccato.category.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.staccato.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            SELECT c FROM Category c
            LEFT JOIN FETCH c.categoryMembers cm
            LEFT JOIN FETCH cm.member
            WHERE c.id = :categoryId
            """)
    Optional<Category> findWithCategoryMembersById(@Param("categoryId") long categoryId);

    @Query("""
            SELECT c FROM Category c
            JOIN c.categoryMembers cm
            WHERE cm.member.id = :memberId
              AND (:isShared IS NULL OR c.isShared = :isShared)
              AND (
                    (c.term.startAt IS NULL AND c.term.endAt IS NULL)
                 OR (:date BETWEEN c.term.startAt AND c.term.endAt)
              )
            """)
    List<Category> findAllByMemberIdAndDateAndSharingFilter(
            @Param("memberId") long memberId,
            @Param("date") LocalDate date,
            @Param("isShared") Boolean isShared);
}
