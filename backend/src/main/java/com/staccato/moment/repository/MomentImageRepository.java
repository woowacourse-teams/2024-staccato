package com.staccato.moment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.moment.domain.MomentImage;

public interface MomentImageRepository extends JpaRepository<MomentImage, Long> {
    Optional<MomentImage> findFirstByMomentId(long momentId);

    @Modifying
    @Query("DELETE FROM MomentImage mi WHERE mi.moment.id In :momentIds")
    void deleteAllByMomentIdInBatch(@Param("momentIds") List<Long> momentIds);
}
