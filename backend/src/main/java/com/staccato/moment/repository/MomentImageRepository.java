package com.staccato.moment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.moment.domain.MomentImage;

public interface MomentImageRepository extends JpaRepository<MomentImage, Long> {
    Optional<MomentImage> findFirstByMomentId(long momentId);
}
