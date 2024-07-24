package com.staccato.pin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.pin.domain.Pin;

public interface PinRepository extends JpaRepository<Pin, Long> {
}
