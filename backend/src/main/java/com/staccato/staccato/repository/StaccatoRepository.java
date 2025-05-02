package com.staccato.staccato.repository;

import java.math.BigDecimal;
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

    @Query("""
            SELECT s
            FROM Staccato s
            JOIN s.category c
            JOIN c.categoryMembers cm
            WHERE cm.member = :member
              AND (
                    (:minLat IS NULL OR :maxLat IS NULL)\s
                    OR s.spot.latitude BETWEEN :minLat AND :maxLat
                  )
              AND (
                    (:minLng IS NULL OR :maxLng IS NULL)\s
                    OR s.spot.longitude BETWEEN :minLng AND :maxLng
                  )
            """)
    List<Staccato> findByMemberAndLocationRange(
            @Param("member") Member member,
            @Param("minLat") BigDecimal swLat,
            @Param("maxLat") BigDecimal neLat,
            @Param("minLng") BigDecimal swLng,
            @Param("maxLng") BigDecimal neLng
    );

    List<Staccato> findAllByCategoryId(long categoryId);

    @Modifying
    @Query("DELETE FROM Staccato s WHERE s.category.id = :categoryId")
    void deleteAllByCategoryIdInBulk(@Param("categoryId") Long categoryId);
}
