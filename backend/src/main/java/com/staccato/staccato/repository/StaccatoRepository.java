package com.staccato.staccato.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            SELECT DISTINCT s
            FROM Staccato s
            JOIN FETCH s.category c
            JOIN FETCH c.categoryMembers cm
            WHERE cm.member = :member
            AND c.id = :categoryId
            AND (
              (:minLat IS NULL OR :maxLat IS NULL OR :minLng IS NULL OR :maxLng IS NULL)
              OR (s.spot.latitude BETWEEN :minLat AND :maxLat AND s.spot.longitude BETWEEN :minLng AND :maxLng)
            )
            """)
    List<Staccato> findByMemberAndLocationRangeAndCategory(
            @Param("member") Member member,
            @Param("minLat") BigDecimal swLat,
            @Param("maxLat") BigDecimal neLat,
            @Param("minLng") BigDecimal swLng,
            @Param("maxLng") BigDecimal neLng,
            @Param("categoryId") Long categoryId
    );

    @Query("""
            SELECT DISTINCT s
            FROM Staccato s
            JOIN FETCH s.category c
            JOIN FETCH c.categoryMembers cm
            WHERE cm.member = :member
            AND (
              (:minLat IS NULL OR :maxLat IS NULL OR :minLng IS NULL OR :maxLng IS NULL)
              OR (s.spot.latitude BETWEEN :minLat AND :maxLat AND s.spot.longitude BETWEEN :minLng AND :maxLng)
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

    @Query(value = """
                SELECT * FROM staccato s
                WHERE s.category_id = :categoryId
                  AND (
                    s.visited_at < :visitedAt
                    OR (s.visited_at = :visitedAt AND s.created_at < :createdAt)
                    OR (s.visited_at = :visitedAt AND s.created_at = :createdAt AND s.id <= :staccatoId)
                  )
                ORDER BY s.visited_at DESC, s.created_at DESC
                LIMIT :limit
            """, nativeQuery = true)
    List<Staccato> findStaccatosByCategoryIdFromCursor(
            @Param("categoryId") long categoryId,
            @Param("staccatoId") long staccatoId,
            @Param("visitedAt") LocalDateTime visitedAt,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("limit") int limit
    );

    @Modifying
    @Query("DELETE FROM Staccato s WHERE s.category.id = :categoryId")
    void deleteAllByCategoryIdInBulk(@Param("categoryId") Long categoryId);

    long countAllByCategoryId(Long id);
}
