package com.staccato.category.repository;

import java.util.stream.Stream;
import jakarta.persistence.QueryHint;
import com.staccato.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.thumbnailUrl FROM Category c WHERE c.thumbnailUrl IS NOT NULL")
    @QueryHints(@QueryHint(name = org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE, value = "100"))
    Stream<String> findAllThumbnailUrls();

}
