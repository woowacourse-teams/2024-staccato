package com.staccato.category.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.domain.CategoryTitle;
import com.staccato.member.domain.Member;

public interface CategoryMemberRepository extends JpaRepository<CategoryMember, Long> {

    @Query("SELECT mm FROM CategoryMember mm JOIN FETCH mm.category WHERE mm.member.id = :memberId")
    List<CategoryMember> findAllByMemberId(long memberId);

    @Query("""
            SELECT mm FROM CategoryMember mm JOIN FETCH mm.category WHERE mm.member.id = :memberId
            AND ((mm.category.term.startAt is null AND mm.category.term.endAt is null)
            or (:date BETWEEN mm.category.term.startAt AND mm.category.term.endAt))
            """)
    List<CategoryMember> findAllByMemberIdAndDate(@Param("memberId") long memberId, @Param("date") LocalDate date);

    boolean existsByMemberAndCategoryTitle(Member member, CategoryTitle title);

    @Modifying
    @Query("DELETE FROM CategoryMember mm WHERE mm.category.id = :categoryId")
    void deleteAllByCategoryIdInBulk(@Param("categoryId") Long categoryId);
}
