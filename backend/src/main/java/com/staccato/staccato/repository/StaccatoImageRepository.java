package com.staccato.staccato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.staccato.domain.StaccatoImage;

public interface StaccatoImageRepository extends JpaRepository<StaccatoImage, Long> {

    @Modifying
    @Query("DELETE FROM StaccatoImage si WHERE si.staccato.id In :staccatoIds")
    void deleteAllByStaccatoIdInBulk(@Param("staccatoIds") List<Long> staccatoIds);

    @Modifying
    @Query("DELETE FROM StaccatoImage si WHERE si.id In :ids")
    void deleteAllByIdInBulk(@Param("ids") List<Long> ids);
}
