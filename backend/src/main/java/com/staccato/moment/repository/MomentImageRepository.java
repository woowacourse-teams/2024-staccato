package com.staccato.moment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.moment.domain.MomentImage;

public interface MomentImageRepository extends JpaRepository<MomentImage, Long> {

    @Modifying
    @Query("DELETE FROM MomentImage mi WHERE mi.moment.id In :momentIds")
    void deleteAllByMomentIdInBulk(@Param("momentIds") List<Long> momentIds);

    @Modifying
    @Query("DELETE FROM MomentImage mi WHERE mi.id In :ids")
    void deleteAllByIdInBulk(@Param("ids") List<Long> ids);
}
