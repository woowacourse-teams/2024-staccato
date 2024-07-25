package com.staccato.travel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.travel.domain.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    Optional<Travel> findByIdAndIsDeletedIsFalse(long id);
}
