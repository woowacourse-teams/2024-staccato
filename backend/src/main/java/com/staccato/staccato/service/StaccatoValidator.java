package com.staccato.staccato.service;

import org.springframework.stereotype.Component;

import com.staccato.exception.StaccatoException;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StaccatoValidator {

    private final StaccatoRepository staccatoRepository;

    public Staccato getStaccatoByIdOrThrow(long staccatoId) {
        return staccatoRepository.findById(staccatoId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }
}
