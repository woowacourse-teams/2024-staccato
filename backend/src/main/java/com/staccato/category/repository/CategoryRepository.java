package com.staccato.category.repository;

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
}
